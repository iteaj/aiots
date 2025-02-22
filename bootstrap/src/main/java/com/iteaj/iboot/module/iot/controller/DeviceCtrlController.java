package com.iteaj.iboot.module.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.websocket.RealtimePushListener;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.CmdInvokeDto;
import com.iteaj.iboot.module.iot.dto.DebugRespDto;
import com.iteaj.iboot.module.iot.dto.DeviceCommandDto;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.entity.ModelApi;
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
import java.util.List;

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
    public DeviceCtrlController(IDeviceService deviceService
            , IProductService productService, IModelApiService modelApiService
            , IotCacheManager cacheManager, RealtimePushListener realtimeListener) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.modelApiService = modelApiService;
        this.cacheManager = cacheManager;
        this.realtimeListener = realtimeListener;
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
    public Mono<Result> ctrl(@PathVariable String modelApiCode, @RequestBody ProtocolModelApiInvokeParam param) {
        Object[] objects = this.getProtocolModelApi(modelApiCode, param);
        DeviceProtocolSupplier supplier = (DeviceProtocolSupplier) objects[3];

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
    @Logger("模型接口调试")
    @CheckPermission(value = {"iot:model:debug", "iot:open:debug"}, logical = Logical.OR)
    @PostMapping("/debug/{modelApiCode}")
    public Mono<Result<DebugRespDto>> debug(@PathVariable String modelApiCode, @RequestBody ProtocolModelApiInvokeParam param) {
        // 获取协议模型接口
        Object[] objects = this.getProtocolModelApi(modelApiCode, param);
        ModelApi modelApi = (ModelApi) objects[1];
        DeviceProtocolSupplier supplier = (DeviceProtocolSupplier) objects[3];

        DebugRespDto debugRespDto = new DebugRespDto(param.getDeviceSn());
        return Mono.<InvokeResult>create(item -> {
            // 调用接口
            try {
                RealtimeStatus realtimeStatus = cacheManager.get(supplier.getProtocol().getCode(), param.buildKey());
                ProtocolInvokeUtil.invoke(supplier, modelApi, param, realtimeStatus, result -> {
                    item.success(result);
                    debugRespDto.setRespTime(System.currentTimeMillis());
                });
            } catch (ProtocolInvokeException e) {
                e.printStackTrace();
                item.success(InvokeResult.fail(e.getMessage(), null));
            }
        }).timeout(Duration.ofSeconds(10), Mono.error(new ServiceException("执行超时"))).map(item -> {
            debugRespDto.setStatus(item.getStatus());
            debugRespDto.setReqMsg(item.getRequestMessage().toString());
            debugRespDto.setRespMsg(item.getResponseMessage().toString());
            if(item.getStatus() == ExecStatus.success) {
                debugRespDto.setValue(item.getValue());
                return success(debugRespDto.setValue(item.getValue()));
            } else {
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

        Device device = deviceService.getByUid(param.getUid())
                .ifNotPresentThrow("查找不到设备[uid=" + param.getUid() + "]").getData();
        param.setDeviceSn(device.getDeviceSn()).addDeviceConfig(device.getConfig());
        // 网关子设备需要取网关设备
        if(product.getDeviceType() == DeviceType.Child) {
            if(device.getPid() == null) {
                throw new ServiceException("此网关子设备没有关联父网关设备");
            }

            if(StrUtil.isBlank(device.getParentDeviceSn())) {
                throw new ServiceException("查找不到绑定的父网关设备");
            }

            param.setParentDeviceSn(device.getParentDeviceSn());
        }

        DeviceProtocolSupplier protocolSupplier = ProtocolSupplierManager.get(product.getProtocolCode());
        if(protocolSupplier == null) {
            throw new ServiceException("没有找到协议[protocol=" + product.getProtocolCode() + "]");
        }

        // 查找对应的协议接口
        AbstractProtocolModelApi protocolModelApi = protocolSupplier.getProtocol().getApi(modelApi.getDirect());
        if(protocolModelApi == null) {
            throw new ServiceException("没有找到接口[apiCode="+modelApi.getDirect()+"]");
        }

        // 设置父网关设备配置
        if(product.getDeviceType() == DeviceType.Child) {
            RealtimeStatus parent = cacheManager.get(protocolSupplier.getProtocol().getCode(), device.getParentDeviceSn());
            if(parent != null) {
                param.addParentDeviceConfig(parent.getConfig());
            }
        }

        return new Object[]{protocolModelApi, modelApi, product, protocolSupplier};
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
        productService.debugById(device.getProductId())
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
    public Mono<Result> invoke(@RequestBody CmdInvokeDto cmd) {
        String cmdCode = cmd.getCode();
        if(StrUtil.isBlank(cmdCode)) {
            return Mono.create((t) -> t.success(fail("未指定指令码")));
        }

        String uid = cmd.getUid();
        boolean isUidBlank = !cmdCode.contains("uid->");
        if(!cmdCode.startsWith("api") || (isUidBlank && StrUtil.isBlank(uid))) {
            return Mono.create((t) -> t.success(fail("指令码格式错误")));
        }

        String[] cmdItem = cmdCode.split("&");
        if(cmdItem.length < 2) {
            return Mono.create((t) -> t.success(fail("指令码格式错误")));
        }

        String modelApiCode = cmdItem[0].split("->")[1];
        uid = isUidBlank ? uid : cmdItem[1].split("->")[1];

        JSONObject params = resolveParams(cmdItem, isUidBlank);
        ProtocolModelApiInvokeParam param = new ProtocolModelApiInvokeParam(uid, params);
        Object[] objects = this.getProtocolModelApi(modelApiCode, param);
        DeviceProtocolSupplier supplier = (DeviceProtocolSupplier) objects[3];

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

    private JSONObject resolveParams(String[] cmdItem, boolean isUidBlank) {
        int length = isUidBlank ? 1 : 2;
        JSONObject result = new JSONObject();
        if(cmdItem.length > length) {
            for (int i = length; i < cmdItem.length; i++) {
                String[] item = cmdItem[i].split("->");
                result.put(item[0], item[1]);
            }
        }

        return result;
    }
}
