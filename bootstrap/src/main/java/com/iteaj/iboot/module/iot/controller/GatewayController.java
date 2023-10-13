package com.iteaj.iboot.module.iot.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.iboot.module.iot.consts.GatewayStatus;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.PortType;
import com.iteaj.iot.serial.SerialComponent;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.entity.Gateway;
import com.iteaj.iboot.module.iot.service.IGatewayService;
import com.iteaj.framework.BaseController;

/**
 * 协议网关管理
 * @author iteaj
 * @since 2023-09-12
 */
@RestController
@RequestMapping("/iot/gateway")
public class GatewayController extends BaseController {

    private final IGatewayService gatewayService;

    public GatewayController(IGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览协议网关功能")
    @CheckPermission({"iot:gateway:view"})
    public Result<IPage<Gateway>> list(Page<Gateway> page, Gateway entity) {
        return this.gatewayService.page(page, entity);
    }

    /**
     * 获取网关列表
     * @param entity
     * @return
     */
    @GetMapping("list")
    public Result<List<Gateway>> list(Gateway entity) {
        return this.gatewayService.list(entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:gateway:edit"})
    public Result<Gateway> getById(Long id) {
        return this.gatewayService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新协议网关记录")
    @CheckPermission(value = {"iot:gateway:edit", "iot:gateway:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Gateway entity) {
        return this.gatewayService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除协议网关记录")
    @CheckPermission({"iot:gateway:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.gatewayService.removeByIds(idList);
    }

    /**
     * 传输协议列表
     * @return
     */
    @GetMapping("transportProtocol")
    public Result<List<IVOption>> transportProtocol() {
        return success(TransportProtocol.options());
    }

    /**
     * 切换网关状态
     * @param id 网关id
     * @param status 状态
     * @return
     */
    @Logger("切换网关状态")
    @PostMapping("switch/{status}")
    @CheckPermission({"iot:gateway:switch"})
    public Result<Boolean> switchStatus(Long id,  @PathVariable GatewayStatus status) {
        this.gatewayService.detailById(id)
            .ifNotPresentThrow("网关不存在")
            .ifPresent(item -> {
                Integer port = item.getPort();
                FrameworkComponent component;
                // 先通过端口获取对应的网络组件
                if(item.getProtocolType() == TransportProtocol.SERIAL) { // 串口协议
                    component = SerialComponent.instance();
                } else if(item.getProtocolType() != TransportProtocol.UDP) { // tcp等其它协议
                    component = FrameworkManager.getInstance().getComponentFactory().get(PortType.Tcp, port);
                } else {// udp协议
                    component = FrameworkManager.getInstance().getComponentFactory().get(PortType.Udp, port);
                }

                // 执行开启的操作
                if(status == GatewayStatus.start) {
                    // 如果网络组件已经注册
                    if(component != null) {
                        component.start(null);
                    } else {
                        DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(item.getProtocolCode());
                        if(supplier != null) {
                            component = supplier.createComponent(null);
                            FrameworkManager.getInstance().register(component).start();
                        } else {
                            throw new ServiceException("未找到对应的协议["+item.getProtocolCode()+"]");
                        }
                    }
                } else if(status == GatewayStatus.stop) { // 执行停止操作
                    FrameworkManager.getInstance().stop(component.getMessageClass());
                }

                this.gatewayService.update(Wrappers.<Gateway>lambdaUpdate()
                        .set(Gateway::getStatus, status)
                        .eq(Gateway::getId, id));
            });

        return success(true);
    }
}

