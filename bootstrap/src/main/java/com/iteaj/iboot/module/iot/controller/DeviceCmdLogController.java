package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.iboot.module.iot.collect.CollectOption;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
import com.iteaj.iboot.module.iot.service.IDeviceCmdLogService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 设备指令下发日志 Controller
 */
@RestController
@RequestMapping("/iot/deviceCmdLog")
public class DeviceCmdLogController extends BaseController {

    private final IDeviceCmdLogService deviceCmdLogService;

    public DeviceCmdLogController(IDeviceCmdLogService deviceCmdLogService) {
        this.deviceCmdLogService = deviceCmdLogService;
    }

    /**
     * 分页列表
     */
    @GetMapping("/view")
    @Logger("浏览设备指令下发日志")
    @CheckPermission({"iot:deviceCmdLog:view"})
    public Result<IPage<DeviceCmdLog>> list(Page<DeviceCmdLog> page, DeviceCmdLog entity) {
        return this.deviceCmdLogService.page(page.addOrder(OrderItem.desc("create_time")), entity);
    }

    /**
     * 详情
     */
    @GetMapping("/edit")
    @CheckPermission({"iot:deviceCmdLog:view"})
    public Result<DeviceCmdLog> getById(Long id) {
        return this.deviceCmdLogService.getById(id);
    }

    /**
     * 删除
     */
    @PostMapping("/del")
    @Logger("删除设备指令下发日志")
    @CheckPermission({"iot:deviceCmdLog:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.deviceCmdLogService.removeByIds(idList);
    }

    /**
     * 执行状态列表
     */
    @GetMapping("/statusList")
    public Result<List<CollectOption>> statusList() {
        return success(Arrays.asList(
                new CollectOption("成功", DeviceCmdLog.STATUS_SUCCESS),
                new CollectOption("失败", DeviceCmdLog.STATUS_FAILURE)
        ));
    }

    /**
     * 下发方式列表
     */
    @GetMapping("/methodList")
    public Result<List<CollectOption>> methodList() {
        return success(Arrays.asList(
                new CollectOption("智能体", DeviceCmdLog.METHOD_AI),
                new CollectOption("大屏", DeviceCmdLog.METHOD_SCREEN),
                new CollectOption("调试", DeviceCmdLog.METHOD_DEBUG),
                new CollectOption("脚本", DeviceCmdLog.METHOD_SCRIPT),
                new CollectOption("场景联动", DeviceCmdLog.METHOD_LINKAGE),
                new CollectOption("移动端", DeviceCmdLog.METHOD_APP)
        ));
    }

    /**
     * 指令类型列表
     */
    @GetMapping("/cmdTypeList")
    public Result<List<CollectOption>> cmdTypeList() {
        return success(Arrays.asList(
                new CollectOption("控制指令", ProtocolApiType.func.name()),
                new CollectOption("事件指令", ProtocolApiType.event.name())
        ));
    }
}
