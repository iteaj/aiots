package com.iteaj.iboot.msn.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.consts.DeviceStatus;
import com.iteaj.iboot.msn.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.service.IDeviceModelService;
import com.iteaj.iboot.msn.iot.service.IDeviceService;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.SocketClient;
import com.iteaj.iot.modbus.client.tcp.ModbusTcpClientComponent;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * modbus设备管理
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
@RestController
@RequestMapping("/iot/modbus")
public class DeviceModbusController extends BaseController {

    private final IDeviceService deviceService;
    private final IDeviceModelService deviceModelService;
    private final ModbusTcpClientComponent tcpClientComponent;
    public DeviceModbusController(IDeviceService deviceService
            , IDeviceModelService deviceModelService
            , @Autowired(required = false) ModbusTcpClientComponent tcpClientComponent) {
        this.deviceService = deviceService;
        this.deviceModelService = deviceModelService;
        this.tcpClientComponent = tcpClientComponent;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @RequiresPermissions({"iot:modbus:view"})
    public Result<IPage<DeviceDto>> list(Page<Device> page, DeviceDto entity) {
        entity.setAlias(DeviceTypeAlias.MODBUS);
        return this.deviceService.pageOfDetail(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:modbus:edit"})
    public Result<Device> detail(Long id) {
        return this.deviceService.getById(id);
    }

    /**
     * 新增或者修改记录
     * @param entity
     */
    @PostMapping("/saveOrUpdate")
    @RequiresPermissions(value = {"iot:modbus:edit", "iot:modbus:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody Device entity) {
        // modbus新增
        DetailResult<Device> one;
        if(entity.getId() == null) {
            one = this.deviceService.getOne(Wrappers.<Device>lambdaQuery()
                    .eq(Device::getIp, entity.getIp())
                    .eq(Device::getPort, entity.getPort()));
        } else {
            one = this.deviceService.getOne(Wrappers.<Device>lambdaQuery()
                    .eq(Device::getIp, entity.getIp())
                    .ne(Device::getId, entity.getId())
                    .eq(Device::getPort, entity.getPort()));
        }

        if(one.ofNullable().isPresent()) {
            return fail("已经包含有设备[" + entity.getIp() + ":" + entity.getPort() +"]");
        }

        return this.deviceService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:modbus:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceService.removeByIds(idList);
    }

    /**
     * 设备连接
     * @param device
     * @return
     */
    @PostMapping("connect/{status}")
    @RequiresPermissions({"iot:modbus:connect"})
    public Result connect(@RequestBody Device device, @PathVariable DeviceStatus status) {
        if(status == null) {
            return fail("未指定连接状态");
        }

        Device entity = deviceService.getById(device.getId()).getData();
        if(entity == null) {
            return fail("设备不存在["+device.getDeviceSn()+"]");
        }

        try {
            ClientConnectProperties properties = new ClientConnectProperties(entity.getIp(), entity.getPort(), entity.getDeviceSn());

            SocketClient client = tcpClientComponent.getClient(properties);
            if(status == DeviceStatus.online) {
                if(client == null) {
                    tcpClientComponent.createNewClientAndConnect(properties);
                } else {
                    client.reconnection();
                }
            } else {
                client.disconnect(false);
            }

            return success(status == DeviceStatus.online ? "连接成功" : "断开成功");
        } catch (Exception e) {
            e.printStackTrace();
            return fail(e.getMessage());
        }
    }
}

