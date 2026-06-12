package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.iboot.module.core.entity.Admin;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.collect.websocket.RealtimePushListener;
import com.iteaj.iboot.module.iot.dto.*;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
import com.iteaj.iboot.module.iot.service.IModelApiInvokeService;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.IotThreadManager;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备指令接口管理
 */
@RestController
@RequestMapping("/iot/instruct")
public class DeviceCtrlController extends BaseController {

    private final IotCacheManager cacheManager;
    private final IModelApiInvokeService modelApiInvokeService;
    private final RealtimePushListener realtimeListener;

    public DeviceCtrlController(IotCacheManager cacheManager,
                                IModelApiInvokeService modelApiInvokeService,
                                RealtimePushListener realtimeListener) {
        this.cacheManager = cacheManager;
        this.modelApiInvokeService = modelApiInvokeService;
        this.realtimeListener = realtimeListener;
    }

    /**
     * 设备控制
     */
    @Logger("模型接口调用")
    @CheckPermission(value = {"iot:model:invoke", "iot:open:invoke"}, logical = Logical.OR)
    @PostMapping("/ctrl/{modelApiCode}")
    public Mono<Result<?>> ctrl(@PathVariable String modelApiCode, @RequestBody ProtocolModelApiInvokeParam param) {
        ModelApiInvokeContext context = modelApiInvokeService.resolve(modelApiCode, param);

        return modelApiInvokeService.invoke(context, param, 10000L).map(item -> {
            if (item.getStatus() == ExecStatus.success) {
                // 将实时结果写出到前端
                IotThreadManager.instance().getExecutorService().execute(() -> {
                    realtimeListener.push(param.getUid(), context.getModelApi(), item);
                });
                return success(item.getValue());
            } else {
                return fail(item.getReason());
            }
        });
    }

    /**
     * 设备调试接口（自定义协议指令）
     */
    @Logger("自定义模型接口调试")
    @CheckPermission(value = {"iot:model:debug", "iot:open:debug"}, logical = Logical.OR)
    @PostMapping("/debug")
    public Mono<Result<DebugRespDto>> debug(@RequestBody ProtocolModelApiInvokeParam param) {
        DeviceCmdLog deviceCmdLog = new DeviceCmdLog().setUid(param.getUid())
                .setMethod(DeviceCmdLog.METHOD_DEBUG).putCmdParams(param.getParam())
                .setOperator(((Admin) SecurityUtil.getLoginUser().orElse(new Admin())).getName())
                .setOperatorId(SecurityUtil.getLoginId().orElse(null)).setCreateTime(new Date());

        try {
            return modelApiInvokeService.debugInvoke(param).map(item -> {
                DebugRespDto debugRespDto = new DebugRespDto(param.getDeviceSn());
                debugRespDto.setStatus(item.getStatus());
                debugRespDto.setRespTime(System.currentTimeMillis());
                debugRespDto.setReqMsg(item.getRequestMessage() != null ? item.getRequestMessage().toString() : "");
                debugRespDto.setRespMsg(item.getResponseMessage() != null ? item.getResponseMessage().toString() : "");
                if (item.getStatus() == ExecStatus.success) {
                    debugRespDto.setValue(item.getValue());
                    deviceCmdLog.setStatus(DeviceCmdLog.STATUS_SUCCESS).setResultMsg("执行成功");
                    IotEvenPublisher.publish(IotEventType.DeviceCmdLog, deviceCmdLog);
                    return success(debugRespDto);
                } else {
                    deviceCmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg(item.getReason());
                    IotEvenPublisher.publish(IotEventType.DeviceCmdLog, deviceCmdLog);
                    return success(debugRespDto.setReason(item.getReason()));
                }
            });
        } catch (ServiceException e) {
            deviceCmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg(e.getMessage());
            IotEvenPublisher.publish(IotEventType.DeviceCmdLog, deviceCmdLog);
            return Mono.just(fail(e.getMessage()));
        }
    }

    /**
     * 设备调试接口（物模型接口）
     */
    @Logger("模型接口调试")
    @CheckPermission(value = {"iot:model:debug", "iot:open:debug"}, logical = Logical.OR)
    @PostMapping("/debug/{modelApiCode}")
    public Mono<Result<DebugRespDto>> debug(@PathVariable String modelApiCode, @RequestBody ProtocolModelApiInvokeParam param) {
        DeviceCmdLog deviceCmdLog = new DeviceCmdLog().setUid(param.getUid())
                .setMethod(DeviceCmdLog.METHOD_DEBUG)
                .putCmdName(param.getConfig("apiName")).putCmdParams(param.getParam())
                .setOperator(((Admin) SecurityUtil.getLoginUser().orElse(new Admin())).getName())
                .setOperatorId(SecurityUtil.getLoginId().orElse(null)).setCreateTime(new Date());

        ModelApiInvokeContext context = modelApiInvokeService.resolve(modelApiCode, param);
        deviceCmdLog.setDeviceName(context.getDevice().getDeviceName()).setCmdType(context.getModelApi().getType().name());

        return modelApiInvokeService.invoke(context, param, 10000L).map(item -> {
            DebugRespDto debugRespDto = new DebugRespDto(param.getDeviceSn());
            debugRespDto.setStatus(item.getStatus());
            debugRespDto.setRespTime(System.currentTimeMillis());
            debugRespDto.setReqMsg(item.getRequestMessage() != null ? item.getRequestMessage().toString() : "");
            debugRespDto.setRespMsg(item.getResponseMessage() != null ? item.getResponseMessage().toString() : "");
            deviceCmdLog.setElapsed(System.currentTimeMillis() - deviceCmdLog.getCreateTime().getTime());

            if (item.getStatus() == ExecStatus.success) {
                debugRespDto.setValue(item.getValue());
                deviceCmdLog.setStatus(DeviceCmdLog.STATUS_SUCCESS).setResultMsg("执行成功");
                if (context.getModelApi().getFuncType() == FuncType.R && item.getValue() != null) {
                    DataSupplier dataSupplier = context.getSupplier().getDataSupplier(
                            item.getProtocol(), param, item.getValue());
                    String value = dataSupplier.getValues().stream()
                            .map(SignalOrFieldValue::toString)
                            .collect(Collectors.joining(", "));
                    debugRespDto.setValue(value);
                }

                IotEvenPublisher.publish(IotEventType.DeviceCmdLog, deviceCmdLog);
                return success(debugRespDto);
            } else {
                deviceCmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg(item.getReason());
                IotEvenPublisher.publish(IotEventType.DeviceCmdLog, deviceCmdLog);
                return success(debugRespDto.setReason(item.getReason()));
            }
        });
    }

    /**
     * 指令集列表
     * @param uid 设备uid
     * @param apiType 指令类型，为空获取所有
     */
    @GetMapping("commands")
    @CheckPermission("iot:cmd:list")
    public Result<List<DeviceCommandDto>> commands(String uid, ProtocolApiType apiType) {
        return success(modelApiInvokeService.listCommands(uid, apiType));
    }

    /**
     * 调用指令
     */
    @Logger("指令接口调用")
    @CheckPermission({"iot:cmd:invoke"})
    @PostMapping("/command/invoke")
    public Mono<Result<?>> invoke(@RequestBody CmdInvokeDto cmd) {
        CmdInvokeUpdateDto updateDto = new CmdInvokeUpdateDto().setScenario(cmd.getScenario())
                .setCode(cmd.getCode()).setUid(cmd.getUid()).setName(cmd.getName());
        return this.updateInvoke(updateDto);
    }

    /**
     * 执行设备功能指令并且更新已写的实时数据状态
     */
    @PostMapping("/command/invoke/update")
    public Mono<Result<?>> updateInvoke(@RequestBody CmdInvokeUpdateDto dto) {
        return modelApiInvokeService.invokeByEvent(dto).map(item -> {
            if (item.getStatus() == ExecStatus.success) {
                return success(item.getValue(), "执行指令成功");
            } else {
                return fail(item.getReason());
            }
        });
    }
}
