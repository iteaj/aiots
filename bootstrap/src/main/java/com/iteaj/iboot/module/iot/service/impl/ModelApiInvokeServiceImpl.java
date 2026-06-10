package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
import com.iteaj.framework.spi.iot.listener.CmdInvokePayload;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.*;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IModelApiInvokeService;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 物模型接口调用服务实现
 */
@Service
public class ModelApiInvokeServiceImpl implements IModelApiInvokeService {

    private final IModelApiService modelApiService;
    private final IProductService productService;
    private final IDeviceService deviceService;
    private final IotCacheManager cacheManager;
    private final RealtimeDataService realtimeDataService;

    public ModelApiInvokeServiceImpl(IModelApiService modelApiService,
                                     IProductService productService,
                                     IDeviceService deviceService,
                                     IotCacheManager cacheManager,
                                     RealtimeDataService realtimeDataService) {
        this.modelApiService = modelApiService;
        this.productService = productService;
        this.deviceService = deviceService;
        this.cacheManager = cacheManager;
        this.realtimeDataService = realtimeDataService;
    }

    @Override
    public ModelApiInvokeContext resolve(String modelApiCode, ProtocolModelApiInvokeParam param) {
        ModelApi modelApi = modelApiService.detailByCode(modelApiCode)
                .ifNotPresentThrow("接口代码不存在[modelApiCode=" + modelApiCode + "]")
                .getData();

        ProductDto product = productService.detailById(modelApi.getProductId())
                .ifNotPresentThrow("未找到所属产品").getData();

        if (product.getStatus() == FuncStatus.disabled) {
            throw new ServiceException("请先启用产品[" + product.getName() + "]");
        }

        RealtimeStatus device = cacheManager.getByUid(param.getUid());
        if (device == null) {
            throw new ServiceException("设备不存在[" + param.getUid() + "]");
        }

        param.setDeviceSn(device.getDeviceSn()).addDeviceConfig(device.getConfig());

        if (product.getDeviceType() == DeviceType.Child) {
            if (StrUtil.isBlank(device.getParentDeviceSn())) {
                throw new ServiceException("查找不到绑定的父网关设备");
            }

            param.setParentDeviceSn(device.getParentDeviceSn());
            RealtimeStatus parent = cacheManager.get(device.getProtocolCode(), device.getParentDeviceSn());
            if (parent != null) {
                param.addParentDeviceConfig(parent.getConfig());
            }
        }

        DeviceProtocolSupplier<?, ?> supplier = ProtocolSupplierManager.get(product.getProtocolCode());
        if (supplier == null) {
            throw new ServiceException("没有找到协议[protocol=" + product.getProtocolCode() + "]");
        }

        AbstractProtocolModelApi protocolModelApi = supplier.getProtocol().getApi(modelApi.getDirect());
        if (protocolModelApi == null) {
            throw new ServiceException("没有找到接口[apiCode=" + modelApi.getDirect() + "]");
        }

        ModelApiInvokeContext context = new ModelApiInvokeContext();
        context.setProtocolModelApi(protocolModelApi);
        context.setModelApi(modelApi);
        context.setProduct(product);
        context.setSupplier(supplier);
        context.setDevice(device);
        return context;
    }

    @Override
    public Mono<InvokeResult> invoke(ModelApiInvokeContext context, ProtocolModelApiInvokeParam param, long timeoutMillis) {
        DeviceProtocolSupplier<?, ?> supplier = context.getSupplier();
        ModelApi modelApi = context.getModelApi();
        RealtimeStatus device = context.getDevice();

        return Mono.<InvokeResult>create(item -> {
            try {
                RealtimeStatus realtimeStatus = cacheManager.get(supplier.getProtocol().getCode(), param.buildKey());
                if (realtimeStatus.getStatus() == DeviceStatus.offline) {
                    if (realtimeStatus.getType() == DeviceType.Direct) {
                        item.success(InvokeResult.fail("设备不在线[" + realtimeStatus.getDeviceName() + "]", null));
                        return;
                    } else if (realtimeStatus.getType() == DeviceType.Child) {
                        RealtimeStatus parent = cacheManager.get(supplier.getProtocol().getCode(), param.getParentDeviceSn());
                        if (parent != null && parent.getStatus() == DeviceStatus.offline) {
                            item.success(InvokeResult.fail("网关设备不在线[" + parent.getDeviceName() + "]", null));
                            return;
                        }
                    }
                }

                ProtocolInvokeUtil.invoke(supplier, modelApi, param, realtimeStatus, item::success);
            } catch (Exception e) {
                item.success(InvokeResult.fail(e.getMessage(), null));
            }
        }).timeout(Duration.ofMillis(timeoutMillis), Mono.error(new ServiceException("执行超时")));
    }

    @Override
    public List<DeviceCommandDto> listCommands(String uid) {
        Device device = deviceService.getByUid(uid)
                .ifNotPresentThrow("设备不存在[" + uid + "]").getData();

        List<DeviceCommandDto> result = new ArrayList<>();
        productService.joinDetailById(device.getProductId())
                .ifNotPresentThrow("此设备数据不完整[" + device.getName() + "]")
                .ifPresent(productDto -> {
                    if (!CollectionUtils.isEmpty(productDto.getFuncApis())) {
                        productDto.getFuncApis().stream()
                                .filter(api -> api.getTriggerMode() == TriggerMode.passive)
                                .forEach(api -> result.addAll(api.resolverCommand(device.getUid(), productDto.getAttrs())));
                    }

                    if (!CollectionUtils.isEmpty(productDto.getEventApis())) {
                        productDto.getEventApis().stream()
                                .filter(api -> api.getTriggerMode() == TriggerMode.passive)
                                .forEach(api -> result.addAll(api.resolverCommand(device.getUid(), productDto.getAttrs())));
                    }
                });

        return result;
    }

    @Override
    public Mono<InvokeResult> invokeByEvent(CmdInvokeUpdateDto dto) {
        CmdInvokePayload cmd = new CmdInvokePayload(dto.getUid(), dto.getCode())
                .setScenario(dto.getScenario()).setName(dto.getName());

        return Mono.<InvokeResult>create(item -> {
            try {
                IotEvenPublisher.publish(IotEventType.CmdInvoke, cmd.build(item::success));
            } catch (ProtocolInvokeException e) {
                item.success(InvokeResult.fail(e.getMessage(), null));
            }
        }).timeout(Duration.ofMillis(cmd.getTimeout()), Mono.just(InvokeResult.fail("执行超时", null)))
        .map(item -> {
            if (item.getStatus() == ExecStatus.success && StrUtil.isNotBlank(dto.getField())) {
                updateRealtimeData(dto);
            }
            return item;
        });
    }

    @Override
    public Mono<InvokeResult> debugInvoke(ProtocolModelApiInvokeParam param) {
        Device device = deviceService.getByUid(param.getUid()).ifNotPresentThrow("设备不存在").getData();
        Product product = productService.getById(device.getProductId()).ifNotPresentThrow("产品不存在").getData();
        if (product.getStatus() == FuncStatus.disabled) {
            throw new ServiceException("产品未启用");
        }
        DeviceProtocolSupplier<?, ?> protocolSupplier = ProtocolSupplierManager.get(product.getProtocolCode());
        if (protocolSupplier == null) {
            throw new ServiceException("未找到驱动[" + product.getProtocolCode() + "]");
        }

        RealtimeStatus byUid = cacheManager.getByUid(param.getUid());
        RealtimeStatus parent = null;
        if (byUid.getType() == DeviceType.Child) {
            parent = cacheManager.get(product.getProtocolCode(), byUid.getParentDeviceSn());
        }

        String direct = param.getString("direct");
        AbstractProtocolModelApi modelApi = protocolSupplier.getProtocol().getApi(direct);
        if (modelApi == null) {
            throw new ServiceException("指令不存在[" + direct + "]");
        }

        ProtocolModelApiInvokeParam invokeParam = ProtocolInvokeUtil.createModelApiInvokeParam(byUid, parent);
        invokeParam.setParam(param.getParam());
        invokeParam.setTimeout(6000L);

        return Mono.<InvokeResult>create(item -> {
            try {
                modelApi.invoke(invokeParam, item::success);
            } catch (Exception e) {
                item.success(InvokeResult.fail(e.getMessage(), null));
            }
        }).timeout(Duration.ofSeconds(10), Mono.error(new ServiceException("执行超时")));
    }

    private void updateRealtimeData(CmdInvokeUpdateDto dto) {
        ProtocolModelApiInvokeParam param = IotUtil.createParamByCmdCode(dto.getCode(), dto.getUid());
        RealtimeStatus device = cacheManager.getByUid(param.getUid());
        DeviceProtocolSupplier<?, ?> protocolSupplier = ProtocolSupplierManager.get(device.getProtocolCode());
        CtrlMode ctrlMode = protocolSupplier.getProtocol().getCtrlMode();
        if (ctrlMode == CtrlMode.POINT) {
            modelApiService.getByCode(param.getApiCode()).ifPresent(modelApi -> {
                if (modelApi.getType() == ProtocolApiType.func) {
                    ModelAttrRealtimeData realtimeData = IotCoreApiService.getRealtimeData(param.getUid(), dto.getField());
                    if (realtimeData != null && realtimeData.getRealtime() != null) {
                        SignalOrFieldValue realtime = realtimeData.getRealtime();
                        realtime.setValue(dto.getValue());
                        realtimeDataService.put(device.getProtocolCode(), device.buildKey(), realtime);
                    }
                }
            });
        }
    }
}
