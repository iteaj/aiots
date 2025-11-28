package com.iteaj.iboot.module.iot.listener;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.listener.CmdInvokeListener;
import com.iteaj.framework.spi.iot.listener.CmdInvokePayload;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class IotViewCmdListener implements CmdInvokeListener {

    private final IotCacheManager cacheManager;
    private final IProductService productService;
    private final IModelApiService modelApiService;

    public IotViewCmdListener(IotCacheManager cacheManager
            , IProductService productService, IModelApiService modelApiService) {
        this.cacheManager = cacheManager;
        this.productService = productService;
        this.modelApiService = modelApiService;
    }

    @Override
    public void onIotEvent(IotEventType eventType, CmdInvokePayload value) {
        ProtocolModelApiInvokeParam invokeParam = null;
        try {
            invokeParam = IotUtil.createParamByCmdCode(value.getCode(), value.getUid());
            RealtimeStatus device = cacheManager.getByUid(invokeParam.getUid());
            if(device == null) {
                value.getCallback().accept(InvokeResult.fail("设备不存在["+value.getUid()+"]", null));
                return;
            }

            if(value.getTimeout() > 0) {
                invokeParam.setTimeout(value.getTimeout());
            }

            //查找对应的物模型api
            ModelApi modelApi = modelApiService.detailByCode(invokeParam.getApiCode())
                    .ifNotPresentThrow("接口代码不存在[apiCode=" + invokeParam.getApiCode() + "]")
                    .getData();

            ProductDto product = this.productService.detailById(modelApi.getProductId())
                    .ifNotPresentThrow("未找到所属产品").getData();

            if(product.getStatus() == FuncStatus.disabled) {
                value.getCallback().accept(InvokeResult.fail("产品未启用["+product.getName()+"]", null));
            }

            final String uid = invokeParam.getUid();
            DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(device.getProtocolCode());
            ProtocolModelApiInvokeParam finalInvokeParam = invokeParam;
            ProtocolInvokeUtil.invoke(supplier, modelApi, invokeParam, device, result -> {
                value.getCallback().accept(result);
                if(result.getStatus() == ExecStatus.success) {
                    DataSupplier dataSupplier = supplier.getDataSupplier(result.getProtocol(), finalInvokeParam, result.getValue());
                    CollectListenerManager.getInstance().publish(dataSupplier.build(new Date()), device, RealtimePayload.TYPE_DATA_CMD);
                    IotLogger.info(IotLogger.NAME, "指令调用", uid
                            , "指令下发成功 指令: {} - 设备: {}", modelApi.getName(), device.buildDeviceKey());
                } else {
                    IotLogger.warn(IotLogger.NAME, "指令调用", uid
                            , "指令下发失败({}) 指令: {} - 设备: {}", result.getReason(), modelApi.getName(), device.buildDeviceKey());
                }
            });

        } catch (ServiceException e) {
            IotLogger.error(IotLogger.NAME, "指令调用", invokeParam != null ? invokeParam.getUid() : null, e.getMessage());
            value.getCallback().accept(InvokeResult.fail(e.getMessage(), null));
        } catch (Exception e) {
            value.getCallback().accept(InvokeResult.fail("未知异常", null));
            IotLogger.error(IotLogger.NAME, "指令调用", invokeParam != null ? invokeParam.getUid() : null, e.getMessage());
        }
    }
}
