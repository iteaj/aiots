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
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.listener.CmdInvokePayload;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.iboot.module.core.entity.Admin;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.websocket.RealtimePushListener;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.*;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.IotThreadManager;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.iteaj.iboot.plugin.protocol.dtu.ModbusProtocolSupplierAutoConfiguration.realtimeDataService;

/**
 * 设备指令接口管理
 */
@RestController
@RequestMapping("/iot/instruct")
public class DeviceCtrlController extends BaseController {

    private final IotCacheManager cacheManager;
    private final IDeviceService deviceService;
    private final IProductService productService;
    private final IModelApiService modelApiService;
    private final RealtimePushListener realtimeListener;
    private final RealtimeDataService realtimeDataService;
    public DeviceCtrlController(IDeviceService deviceService
            , IProductService productService, IModelApiService modelApiService
            , IotCacheManager cacheManager, RealtimePushListener realtimeListener
            , RealtimeDataService realtimeDataService) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.modelApiService = modelApiService;
        this.cacheManager = cacheManager;
        this.realtimeListener = realtimeListener;
        this.realtimeDataService = realtimeDataService;
    }

    /**
     * 设备控制
     * @param modelApiCode 物模型接口代码
     * @param param 调用参数
     * @return
     */
    @Logger("模型接口调用")
    @CheckPermission(value = {"iot:model:invoke", "iot:open:invoke"}, logical = Logical.OR)
    @PostMapping("/ctrl/{modelApiCode}")
    public Mono<Result<?>> ctrl(@PathVariable String modelApiCode, @RequestBody ProtocolModelApiInvokeParam param) {
        Object[] objects = this.getProtocolModelApi(modelApiCode, param);
        DeviceProtocolSupplier<?, ?> supplier = (DeviceProtocolSupplier<?, ?>) objects[3];

        return Mono.<InvokeResult>create(item -> {
            try {
                RealtimeStatus realtimeStatus = cacheManager.get(supplier.getProtocol().getCode(), param.buildKey());
                if(realtimeStatus.getStatus() == DeviceStatus.offline) {
                    if(realtimeStatus.getType() == DeviceType.Direct) {
                        item.success(InvokeResult.fail("设备不在线["+realtimeStatus.getDeviceName()+"]", null)); return;
                    } else if(realtimeStatus.getType() == DeviceType.Child) {
                        RealtimeStatus parent = cacheManager.get(supplier.getProtocol().getCode(), param.getParentDeviceSn());
                        if(parent != null && parent.getStatus() == DeviceStatus.offline) {
                            item.success(InvokeResult.fail("网关设备不在线["+parent.getDeviceName()+"]", null)); return;
                        }
                    }
                }

                ProtocolInvokeUtil.invoke(supplier, (ModelApi) objects[1], param, realtimeStatus, result -> {
                    item.success(result);
                    // 将实时结果写出到前端
                    IotThreadManager.instance().getExecutorService().execute(() -> {
                        realtimeListener.push(param.getUid(), (ModelApi) objects[1], result);
                    });
                });
            } catch (ProtocolInvokeException e) {
                e.printStackTrace();
                item.success(InvokeResult.fail(e.getMessage(), null));
            }
        }).timeout(Duration.ofSeconds(10), Mono.error(new ServiceException("执行超时"))).map(item -> {
            if(item.getStatus() == ExecStatus.success) {
                return success(item.getValue());
            } else {
                return fail(item.getReason());
            }
        });
    }


    /**
     * 设备调试接口
     * @return
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
            Device device = deviceService.getByUid(param.getUid()).ifNotPresentThrow("设备不存在").getData();
            deviceCmdLog.setDeviceName(device.getName());
            Product product = productService.getById(device.getProductId()).ifNotPresentThrow("产品不存在").getData();
            if(product.getStatus() == FuncStatus.disabled) {
                throw new ServiceException("产品未启用");
            }
            DeviceProtocolSupplier<?, ?> protocolSupplier = ProtocolSupplierManager.get(product.getProtocolCode());
            if(protocolSupplier == null) {
                throw new ServiceException("未找到驱动[" + product.getProtocolCode() + "]");
            }

            DebugRespDto debugRespDto = new DebugRespDto(device.getDeviceSn());
            RealtimeStatus parent = null;
            RealtimeStatus byUid = cacheManager.getByUid(param.getUid());
            if(byUid.getType() == DeviceType.Child) {
                parent = cacheManager.get(product.getProtocolCode(), byUid.getParentDeviceSn());
            }

            String direct = param.getString("direct");
            AbstractProtocolModelApi modelApi = protocolSupplier.getProtocol().getApi(direct);
            if(modelApi == null) {
                throw new ServiceException("指令不存在["+direct+"]");
            }

            deviceCmdLog.setCmdType(modelApi.getType().name()).putCmdName(modelApi.getName() + " -> 自定义调试");
            ProtocolModelApiInvokeParam modelApiInvokeParam = ProtocolInvokeUtil.createModelApiInvokeParam(byUid, parent);
            modelApiInvokeParam.setParam(param.getParam());
            modelApiInvokeParam.setTimeout(6000L);
            return Mono.<InvokeResult>create(item -> {
                try {
                    modelApi.invoke(modelApiInvokeParam, item::success);
                } catch (Exception e) {
                    e.printStackTrace();
                    item.success(InvokeResult.fail(e.getMessage(), null));
                }
            }).timeout(Duration.ofSeconds(10), Mono.error(new ServiceException("执行超时"))).map(item -> {
                debugRespDto.setStatus(item.getStatus());
                debugRespDto.setRespTime(System.currentTimeMillis());
                debugRespDto.setReqMsg(item.getRequestMessage().toString());
                debugRespDto.setRespMsg(item.getResponseMessage().toString());
                if(item.getStatus() == ExecStatus.success) {
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
     * 设备调试接口
     * @return
     */
    @Logger("模型接口调试")
    @CheckPermission(value = {"iot:model:debug", "iot:open:debug"}, logical = Logical.OR)
    @PostMapping("/debug/{modelApiCode}")
    public Mono<Result<DebugRespDto>> debug(@PathVariable String modelApiCode, @RequestBody ProtocolModelApiInvokeParam param) {
        // 记录日志
        DeviceCmdLog deviceCmdLog = new DeviceCmdLog().setUid(param.getUid())
                .setMethod(DeviceCmdLog.METHOD_DEBUG)
                .putCmdName(param.getConfig("apiName")).putCmdParams(param.getParam())
                .setOperator(((Admin) SecurityUtil.getLoginUser().orElse(new Admin())).getName())
                .setOperatorId(SecurityUtil.getLoginId().orElse(null)).setCreateTime(new Date());

        // 获取协议模型接口
        Object[] objects = this.getProtocolModelApi(modelApiCode, param);
        ModelApi modelApi = (ModelApi) objects[1];
        RealtimeStatus device = (RealtimeStatus) objects[4];
        DeviceProtocolSupplier<?, ?> supplier = (DeviceProtocolSupplier<?, ?>) objects[3];

        deviceCmdLog.setDeviceName(device.getDeviceName()).setCmdType(modelApi.getType().name());

        DebugRespDto debugRespDto = new DebugRespDto(param.getDeviceSn());
        return Mono.<InvokeResult>create(item -> {
            // 调用接口
            try {
                RealtimeStatus realtimeStatus = cacheManager.get(supplier.getProtocol().getCode(), param.buildKey());
                ProtocolInvokeUtil.invoke(supplier, modelApi, param, realtimeStatus, item::success);
            } catch (Exception e) {
                e.printStackTrace();
                item.success(InvokeResult.fail(e.getMessage(), null));
            }
        }).timeout(Duration.ofSeconds(10), Mono.error(new ServiceException("执行超时"))).map(item -> {
            debugRespDto.setStatus(item.getStatus());
            debugRespDto.setRespTime(System.currentTimeMillis());
            debugRespDto.setReqMsg(item.getRequestMessage().toString());
            debugRespDto.setRespMsg(item.getResponseMessage().toString());
            deviceCmdLog.setElapsed(System.currentTimeMillis() - deviceCmdLog.getCreateTime().getTime());
            if(item.getStatus() == ExecStatus.success) {
                debugRespDto.setValue(item.getValue());
                deviceCmdLog.setStatus(DeviceCmdLog.STATUS_SUCCESS).setResultMsg("执行成功");
                if(modelApi.getFuncType() == FuncType.R && item.getValue() != null) {
                    final DataSupplier dataSupplier = supplier.getDataSupplier(item.getProtocol(), param, item.getValue());
                    final String value = dataSupplier.getValues().stream()
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

    private Object[] getProtocolModelApi(String modelApiCode, ProtocolModelApiInvokeParam param) {
        //查找对应的物模型api
        ModelApi modelApi = modelApiService.detailByCode(modelApiCode)
                .ifNotPresentThrow("接口代码不存在[modelApiCode=" + modelApiCode + "]")
                .getData();

        ProductDto product = this.productService.detailById(modelApi.getProductId())
                .ifNotPresentThrow("未找到所属产品").getData();

        if(product.getStatus() == FuncStatus.disabled) {
            throw new ServiceException("请先启用产品["+product.getName()+"]");
        }

        final RealtimeStatus device = cacheManager.getByUid(param.getUid());
        if(device == null) {
            throw new ServiceException("设备不存在["+param.getUid()+"]");
        }

        param.setDeviceSn(device.getDeviceSn()).addDeviceConfig(device.getConfig());
        // 网关子设备需要取网关设备
        if(product.getDeviceType() == DeviceType.Child) {
            if(StrUtil.isBlank(device.getParentDeviceSn())) {
                throw new ServiceException("查找不到绑定的父网关设备");
            }

            param.setParentDeviceSn(device.getParentDeviceSn());
            RealtimeStatus parent = cacheManager.get(device.getProtocolCode(), device.getParentDeviceSn());
            if(parent != null) {
                param.addParentDeviceConfig(parent.getConfig());
            }
        }

        DeviceProtocolSupplier<?, ?> protocolSupplier = ProtocolSupplierManager.get(product.getProtocolCode());
        if(protocolSupplier == null) {
            throw new ServiceException("没有找到协议[protocol=" + product.getProtocolCode() + "]");
        }

        // 查找对应的协议接口
        AbstractProtocolModelApi protocolModelApi = protocolSupplier.getProtocol().getApi(modelApi.getDirect());
        if(protocolModelApi == null) {
            throw new ServiceException("没有找到接口[apiCode="+modelApi.getDirect()+"]");
        }

        return new Object[]{protocolModelApi, modelApi, product, protocolSupplier, device};
    }

    /**
     * 指令集列表
     * @param uid
     * @return
     */
    @GetMapping("commands")
    @CheckPermission("iot:cmd:list")
    public Result<List<DeviceCommandDto>> commands(String uid) {
        Device device = this.deviceService.getByUid(uid)
                .ifNotPresentThrow("设备不存在[" + uid + "]").getData();

        List<DeviceCommandDto> result = new ArrayList<>();
        productService.joinDetailById(device.getProductId())
                .ifNotPresentThrow("此设备数据不完整["+device.getName()+"]")
                .ifPresent(productDto -> {
                    if(!CollectionUtils.isEmpty(productDto.getFuncApis())) {
                        productDto.getFuncApis().stream()
                                .filter(api -> api.getTriggerMode() == TriggerMode.passive)
                                .forEach(api -> result.addAll(api.resolverCommand(device.getUid(), productDto.getAttrs())));
                    }

                    if(!CollectionUtils.isEmpty(productDto.getEventApis())) {
                        productDto.getEventApis().stream()
                                .filter(api -> api.getTriggerMode() == TriggerMode.passive)
                                .forEach(api -> result.addAll(api.resolverCommand(device.getUid(), productDto.getAttrs())));
                    }
                });

        return success(result);
    }

    /**
     * 调用指令
     * @param cmd 调用参数
     * @return
     */
    @Logger("指令接口调用")
    @CheckPermission({"iot:cmd:invoke"})
    @PostMapping("/command/invoke")
    public Mono<Result<?>> invoke(@RequestBody CmdInvokeDto cmd) {
        CmdInvokeUpdateDto updateDto = new CmdInvokeUpdateDto().setScenario(cmd.getScenario())
                .setCode(cmd.getCode()).setUid(cmd.getUid()).setName(cmd.getName());
        return this.cmd(updateDto);
    }

    /**
     * 执行设备功能指令并且更新已写的实时数据状态
     * @param dto 参数
     */
    @ResponseBody
    @PostMapping("/command/invoke/update")
    public Mono<Result<?>> cmd(@RequestBody CmdInvokeUpdateDto dto) {
        CmdInvokePayload cmd = new CmdInvokePayload(dto.getUid(), dto.getCode())
                .setScenario(dto.getScenario()).setName(dto.getName());
        return Mono.<InvokeResult>create(item -> {
            try {
                IotEvenPublisher.publish(IotEventType.CmdInvoke, cmd.build(item::success));
            } catch (ProtocolInvokeException e) {
                item.success(InvokeResult.fail(e.getMessage(), null));
            }
        }).timeout(Duration.ofMillis(cmd.getTimeout()), Mono.just(InvokeResult.fail("执行超时", null))).map(item -> {
            if(item.getStatus() == ExecStatus.success) {
                if(StrUtil.isNotBlank(dto.getField())) {
                    ProtocolModelApiInvokeParam param = IotUtil.createParamByCmdCode(cmd.getCode(), cmd.getUid());
                    // 将实时值更新为下发的值
                    RealtimeStatus device = cacheManager.getByUid(param.getUid());
                    DeviceProtocolSupplier<?, ?> protocolSupplier = ProtocolSupplierManager.get(device.getProtocolCode());
                    CtrlMode ctrlMode = protocolSupplier.getProtocol().getCtrlMode();
                    if(ctrlMode == CtrlMode.POINT) {
                        modelApiService.getByCode(param.getApiCode()).ifPresent(modelApi -> {
                            if(modelApi.getType() == ProtocolApiType.func) {
                                ModelAttrRealtimeData realtimeData = IotCoreApiService.getRealtimeData(param.getUid(), dto.getField());
                                if(realtimeData != null && realtimeData.getRealtime() != null) {
                                    SignalOrFieldValue realtime = realtimeData.getRealtime();
                                    realtime.setValue(dto.getValue());
                                    realtimeDataService.put(device.getProtocolCode(), device.buildKey(), realtime);
                                }
                            }
                        });
                    }
                }
                return success(item.getValue(), "执行指令成功");
            } else {
                return fail(item.getReason());
            }
        });
    }
}
