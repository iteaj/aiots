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
import com.iteaj.iot.client.mqtt.MqttClient;
import com.iteaj.iot.client.mqtt.MqttConnectProperties;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttComponent;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * mqtt管理
 *
 * @author iteaj
 * @since 2022-11-03
 */
@RestController
@RequestMapping("/iot/mqtt")
public class DeviceMqttController extends BaseController {

    private final IDeviceService deviceService;
    private final IDeviceModelService deviceModelService;
    private final DefaultMqttComponent defaultMqttComponent;

    public DeviceMqttController(IDeviceService deviceService
            , IDeviceModelService deviceModelService
            , @Autowired(required = false) DefaultMqttComponent defaultMqttComponent) {
        this.deviceService = deviceService;
        this.deviceModelService = deviceModelService;
        this.defaultMqttComponent = defaultMqttComponent;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @RequiresPermissions({"iot:mqtt:view"})
    public Result<IPage<DeviceDto>> list(Page<Device> page, DeviceDto entity) {
        entity.setAlias(DeviceTypeAlias.MQTT);
        return this.deviceService.pageOfDetail(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @RequiresPermissions({"iot:mqtt:edit"})
    public Result<Device> getEditDetail(Long id) {
        return this.deviceService.getById(id);
    }

    /**
     * 新增或者修改记录
     * @param entity
     */
    @PostMapping("/saveOrUpdate")
    @RequiresPermissions(value = {"iot:mqtt:edit", "iot:mqtt:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody Device entity) {
        // plc新增
        DetailResult<Device> one;
        if(entity.getId() == null) {
            one = this.deviceService.getOne(Wrappers.<Device>lambdaQuery()
                    .eq(Device::getDeviceSn, entity.getDeviceSn()));
        } else {
            one = this.deviceService.getOne(Wrappers.<Device>lambdaQuery()
                    .eq(Device::getDeviceSn, entity.getDeviceSn())
                    .ne(Device::getId, entity.getId()));
        }

        if(one.getData() != null) {
            return fail("已经包含有设备[" + entity.getDeviceSn() +"]");
        }

        return this.deviceService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @RequiresPermissions({"iot:mqtt:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceService.removeByIds(idList);
    }

    /**
     * 设备连接
     * @param device
     * @return
     */
    @PostMapping("connect")
    @RequiresPermissions({"iot:mqtt:connect"})
    public Result connect(@RequestBody Device device, DeviceStatus status) {
        if(status == null) {
            return fail("未指定连接状态");
        }

        if(this.defaultMqttComponent == null) {
            return fail("没有启用mqtt组件");
        }

        Device entity = deviceService.getById(device.getId()).getData();
        if(entity == null) {
            return fail("设备不存在["+device.getDeviceSn()+"]");
        }

        MqttConnectProperties properties = new MqttConnectProperties(entity.getIp(), entity.getPort(), entity.getDeviceSn());
        properties.setUsername(entity.getAccount());
        properties.setPassword(entity.getPassword());
        MqttClient client = this.defaultMqttComponent.getClient(properties);
        if(status == DeviceStatus.online) {
            if(client == null) {
                this.defaultMqttComponent.createNewClientAndConnect(properties);
            } else {
                client.connect(null, 3000).syncUninterruptibly();
            }
        } else {
            if(client != null) {
                client.disconnect(false).syncUninterruptibly();
            }
        }

        return success();
    }
}

