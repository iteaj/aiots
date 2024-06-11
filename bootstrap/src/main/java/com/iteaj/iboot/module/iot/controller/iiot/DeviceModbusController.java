package com.iteaj.iboot.module.iot.controller.iiot;

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
import com.iteaj.iboot.module.iot.consts.SerialStatus;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.entity.Serial;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.ISerialService;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.SocketClient;
import com.iteaj.iot.modbus.client.tcp.ModbusTcpClientComponent;
import com.iteaj.framework.security.CheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * modbus设备管理
 *
 * @author iteaj
 * @since 2022-05-15
 */
@RestController
@RequestMapping("/iot/modbus")
public class DeviceModbusController extends BaseController {

    private final ISerialService serialService;
    private final IDeviceService deviceService;
    private final IProductService deviceModelService;
    private final ModbusTcpClientComponent tcpClientComponent;
    public DeviceModbusController(ISerialService serialService, IDeviceService deviceService
            , IProductService deviceModelService
            , @Autowired(required = false) ModbusTcpClientComponent tcpClientComponent) {
        this.serialService = serialService;
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
    @CheckPermission({"iot:modbus:view"})
    public Result<IPage<DeviceDto>> list(Page<Device> page, DeviceDto entity) {
        entity.setAlias(DeviceTypeAlias.MODBUS);
        return this.deviceService.pageOfDetail(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:modbus:edit"})
    public Result<Device> detail(Long id) {
        return this.deviceService.getById(id);
    }

    /**
     * 新增或者修改记录
     * @param entity
     */
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"iot:modbus:edit", "iot:modbus:add"}, logical = Logical.OR)
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
    @CheckPermission({"iot:modbus:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceService.removeByIds(idList);
    }

    /**
     * 设备连接
     * @param device
     * @return
     */
    @PostMapping("connect/{status}")
    @CheckPermission({"iot:modbus:connect"})
    public Result connect(@RequestBody Device device, @PathVariable DeviceStatus status) {
        if(status == null) {
            return fail("未指定连接状态");
        }

        DeviceDto entity = deviceService.detailById(device.getId()).getData();
        if(entity == null) {
            return fail("设备不存在["+device.getDeviceSn()+"]");
        }

        try {
            return success();
        } catch (Exception e) {
            e.printStackTrace();
            return fail(e.getMessage());
        }
    }
}

