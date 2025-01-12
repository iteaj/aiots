package com.iteaj.iboot.module.iot.controller.iiot;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.iot.consts.ProtocolCodes;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dlt645设备管理
 *
 * @author iteaj
 * @since 2022-11-03
 */
@RestController
@RequestMapping("/iot/dlt645")
public class DeviceDlt645Controller extends BaseController {

    private final IDeviceService deviceService;
    private final IProductService productService;

    public DeviceDlt645Controller(IDeviceService deviceService
            , IProductService productService) {
        this.deviceService = deviceService;
        this.productService = productService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @CheckPermission({"iot:dlt645:view"})
    public Result<IPage<DeviceDto>> list(Page<Device> page, DeviceDto entity) {
        entity.setProtocolCodes(new String[]{ProtocolCodes.DTU_DLT645.getValue()
                        ,ProtocolCodes.SERIAL_DLT645.getValue()});
        return this.deviceService.pageOfDetail(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    public Result<Device> getEditDetail(Long id) {
        return this.deviceService.getById(id);
    }

    /**
     * 新增或者修改记录
     * @param entity
     */
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"iot:dlt645:edit", "iot:dlt645:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody Device entity) {
        return this.deviceService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:dlt645:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceService.removeByIds(idList);
    }


    /**
     * 获取dlt645产品列表
     * @param deviceType 设备类型
     * @return
     */
    @GetMapping("/products")
    public Result<List<Product>> products(DeviceType deviceType) {
        return this.productService.listOfProtocolCodes(deviceType
                , new String[]{ProtocolCodes.DTU_DLT645.getValue(), ProtocolCodes.SERIAL_DLT645.getValue()}
                , null);
    }
}

