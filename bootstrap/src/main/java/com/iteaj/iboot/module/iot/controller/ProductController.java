package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.consts.GatewayStatus;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.TcpSocketClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品管理
 *
 * @author iteaj
 * @since 2022-07-22
 */
@RestController
@RequestMapping("/iot/product")
public class ProductController extends BaseController {

    private final IDeviceService deviceService;
    private final IProductService productService;

    public ProductController(IDeviceService deviceService, IProductService productService) {
        this.deviceService = deviceService;
        this.productService = productService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @CheckPermission({"iot:product:view"})
    public Result<IPage<ProductDto>> list(Page<Product> page, Product entity) {
        return this.productService.pageOfDetail(page, entity);
    }

    /**
     * 获取指定类型下面的列表
     * @param productTypeId -1将获取所有
     * @return
     */
    @GetMapping("/listByType")
    public Result<List<ProductDto>> listByType(Long productTypeId) {
        Product product = new Product();
        product.setProductTypeId(productTypeId);
        return this.productService.listOfDetail(product);
    }

    /**
     * 父产品列表
     * @return
     */
    @GetMapping("parent")
    public Result<List<Product>> parent() {
        return this.productService.list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getDeviceType, DeviceType.Gateway));
    }


    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:product:edit"})
    public Result<Product> getById(Long id) {
        return this.productService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"iot:product:edit", "iot:product:add"}, logical = Logical.OR)
    public Result<Boolean> save(@Validated @RequestBody Product entity) {
        return this.productService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:product:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.productService.removeByIds(idList);
    }

    /**
     * 设备类型列表
     * @return
     */
    @GetMapping("deviceTypes")
    public Result<List<IVOption>> deviceTypes() {
        return success(DeviceType.options());
    }

    /**
     * 切换产品状态
     * @param id 产品id
     * @param status 状态
     * @return
     */
    @Logger("切换产品状态")
    @PostMapping("switch/{id}/{status}")
    @CheckPermission({"iot:gateway:switch"})
    public Result<Boolean> switchStatus(@PathVariable Long id,  @PathVariable FuncStatus status) {
        this.productService.detailById(id)
            .ifNotPresentThrow("查找不到此产品")
            .ifPresent(item -> {
                // 网关子设备的父产品是边缘网关产品
                if(item.getDeviceType() == DeviceType.Child) {
                    if(status == FuncStatus.enabled) {
                        this.productService.getById(item.getParentId())
                                .ifNotPresentThrow("查找不到父产品["+item.getGatewayTypeName()+"]")
                                .ifPresent(parent -> {
                                    if(parent.getStatus() == FuncStatus.disabled) {
                                        throw new ServiceException("请先启用父产品["+parent.getName()+"]");
                                    }
                                });
                    }
                } else if(item.getDeviceType() == DeviceType.Gateway) { // 启用边缘网关下面所有的设备
                    DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(item.getProtocolCode());
                    if(supplier == null) {
                        throw new ServiceException("查找不到对应的协议提供[code="+item.getProtocolCode()+"]");
                    }

                    if(supplier instanceof ClientProtocolSupplier) {
                        // 先校验产品绑定的网关是否启用
                        if(status == FuncStatus.enabled) {
                            if(item.getGatewayStatus() == GatewayStatus.stop) {
                                throw new ServiceException("请先启用网关["+item.getGatewayName()+"]");
                            }
                        }

                        this.deviceService.listByProductId(item.getId()).forEach(device -> {
                            try {
                                NetworkConfig networkConfig = createClientConfig(supplier, device);
                                IotClient client = ((ClientProtocolSupplier) supplier).createClient(networkConfig);
                                // 处理网关产品设备的状态
                                resolveGatewayProductClientStatus(client, device, status);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }

                this.productService.update(Wrappers.<Product>lambdaUpdate()
                        .eq(Product::getId, item.getId())
                        .set(Product::getStatus, status));
            });
        return success(true);
    }

    private void resolveGatewayProductClientStatus(IotClient client, DeviceDto device, FuncStatus status) {
        if(status == FuncStatus.enabled) {
            if(client instanceof TcpSocketClient) {
                if(!((TcpSocketClient) client).isConnect()) {
                    client.connect();
                }
            }
        } else if(status == FuncStatus.disabled) {
            if(client instanceof TcpSocketClient) {
                if(((TcpSocketClient) client).isConnect()) {
                    client.disconnect();
                }

                // 更新此网关设备下面所有子设备的状态
                deviceService.update(Wrappers.<Device>lambdaUpdate()
                        .set(Device::getStatus, DeviceStatus.offline)
                        .eq(Device::getPid, device.getId()));
            }
        } else {
            throw new ServiceException("错误的状态[status="+status+"]");
        }
    }

    private NetworkConfig createClientConfig(DeviceProtocolSupplier supplier, DeviceDto device) {
        TransportProtocol type = supplier.getProtocol().getType();
        if(type == TransportProtocol.TCP) {
            NetworkConfigImpl.TcpNetworkConfig config = new NetworkConfigImpl.TcpNetworkConfig();
            config.setHost(device.getIp());
            config.setPort(device.getPort());
            config.setDeviceSn(device.getDeviceSn());
            return config;
        }
        return null;
    }
}

