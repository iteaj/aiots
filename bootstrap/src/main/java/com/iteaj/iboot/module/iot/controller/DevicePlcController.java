package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.entity.DeviceModel;
import com.iteaj.iboot.module.iot.service.IDeviceModelService;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iot.client.SocketClient;
import com.iteaj.iot.plc.omron.OmronComponent;
import com.iteaj.iot.plc.omron.OmronConnectProperties;
import com.iteaj.iot.plc.siemens.SiemensConnectProperties;
import com.iteaj.iot.plc.siemens.SiemensModel;
import com.iteaj.iot.plc.siemens.SiemensS7Component;
import com.iteaj.framework.security.CheckPermission;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * plc设备管理
 *
 * @author iteaj
 * @since 2022-05-15
 */
@RestController
@RequestMapping("/iot/plc")
public class DevicePlcController extends BaseController {

    private final IDeviceService deviceService;
    private final OmronComponent omronComponent;
    private final IDeviceModelService deviceModelService;
    private final SiemensS7Component siemensS7Component;
    public DevicePlcController(IDeviceService deviceService
            , OmronComponent omronComponent, IDeviceModelService deviceModelService
            , SiemensS7Component siemensS7Component) {
        this.deviceService = deviceService;
        this.omronComponent = omronComponent;
        this.deviceModelService = deviceModelService;
        this.siemensS7Component = siemensS7Component;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @CheckPermission({"iot:plc:view"})
    public Result<IPage<DeviceDto>> list(Page<Device> page, DeviceDto entity) {
        entity.setAlias(DeviceTypeAlias.PLC);
        return this.deviceService.pageOfDetail(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:plc:edit"})
    public Result<Device> getEditDetail(Long id) {
        return this.deviceService.getById(id);
    }

    /**
     * 新增或者修改记录
     * @param entity
     */
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"iot:plc:edit", "iot:plc:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody Device entity) {
        // plc新增
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

        if(one.getData() != null) {
            return fail("已经包含有设备[" + entity.getIp() + ":" + entity.getPort() +"]");
        }

        return this.deviceService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:plc:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceService.removeByIds(idList);
    }

    /**
     * 设备连接
     * @param device
     * @return
     */
    @PostMapping("connect")
    @CheckPermission({"iot:plc:connect"})
    public Result connect(@RequestBody Device device, DeviceStatus status) {
        if(status == null) {
            return fail("未指定连接状态");
        }

        Device entity = deviceService.getById(device.getId()).getData();
        if(entity == null) {
            return fail("设备不存在["+device.getDeviceSn()+"]");
        }

        DeviceModel data = deviceModelService.getById(entity.getModel()).getData();
        if(data == null) {
            return fail("错误的设备型号");
        }

        try {
            // 西门子设备
            if(device.getDeviceTypeId() == 2) {
                SiemensModel model = SiemensModel.valueOf(data.getModel());
                SiemensConnectProperties properties = new SiemensConnectProperties(device.getIp(), device.getPort(), model, entity.getDeviceSn());
                SocketClient client = siemensS7Component.getClient(properties);
                if(status == DeviceStatus.online) {
                    if(client == null) {
                        siemensS7Component.createNewClientAndConnect(properties);
                    } else {
                        client.reconnection();
                    }
                } else {
                    client.disconnect();
                }
            } else if(device.getDeviceTypeId() == 3) { // 欧姆龙设备
                OmronConnectProperties properties = new OmronConnectProperties(device.getIp(), device.getPort());
                properties.setConnectKey(entity.getDeviceSn());
                SocketClient client = omronComponent.getClient(properties);
                if(status == DeviceStatus.online) {
                    if(client == null) {
                        omronComponent.createNewClientAndConnect(properties);
                    } else {
                        client.connect(null, 3000);
                    }
                } else {
                    client.disconnect();
                }
            }

            return success(status == DeviceStatus.online ? "连接成功" : "断开成功");
        } catch (Exception e) {
            e.printStackTrace();
            return fail(e.getMessage());
        }
    }
}

