package com.iteaj.iboot.module.iot.listener;

import com.iteaj.framework.Entity;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.listener.*;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.module.core.entity.Admin;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
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
        DeviceCmdLog cmdLog = new DeviceCmdLog().setOperator("-").putCmdName(value.getName())
                .setMethod(value.getScenario()).setCreateTime(new Date());
        try {
            if(SecurityUtil.isLogin()) {
                if(SecurityUtil.getLoginUser().isPresent()) {
                    Admin entity = (Admin)SecurityUtil.getLoginUser().get();
                    cmdLog.setOperatorId(entity.getId()).setOperator(entity.getName());
                }
            }
            invokeParam = IotUtil.createParamByCmdCode(value.getCode(), value.getUid());
            cmdLog.setUid(invokeParam.getUid()).putCmdParams(invokeParam.getParam());
            RealtimeStatus device = cacheManager.getByUid(invokeParam.getUid());
            if(device == null) {
                cmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg("设备不存在");
                value.getCallback().accept(InvokeResult.fail("设备不存在["+value.getUid()+"]", null));
                IotEvenPublisher.publish(IotEventType.DeviceCmdLog, cmdLog);
                return;
            }
            cmdLog.setDeviceName(device.getDeviceName());

            if(value.getTimeout() > 0) {
                invokeParam.setTimeout(value.getTimeout());
            }

            //查找对应的物模型api
            ModelApi modelApi = modelApiService.detailByCode(invokeParam.getApiCode())
                    .ifNotPresentThrow("接口代码不存在[apiCode=" + invokeParam.getApiCode() + "]")
                    .getData();
            cmdLog.setCmdType(modelApi.getType().name());
            if(value.getName() == null) {
                cmdLog.putCmdName(modelApi.getName());
            }

            ProductDto product = this.productService.detailById(device.getProductId())
                    .ifNotPresentThrow("未找到所属产品").getData();

            if(product.getStatus() == FuncStatus.disabled) {
                cmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg("产品未启用");
                IotEvenPublisher.publish(IotEventType.DeviceCmdLog, cmdLog);
                value.getCallback().accept(InvokeResult.fail("产品未启用["+product.getName()+"]", null));
                return;
            }

            final String uid = invokeParam.getUid();
            DeviceProtocolSupplier<?, ?> supplier = ProtocolSupplierManager.get(device.getProtocolCode());
            ProtocolModelApiInvokeParam finalInvokeParam = invokeParam;
            ProtocolInvokeUtil.invoke(supplier, modelApi, invokeParam, device, result -> {
                if(result.getStatus() == ExecStatus.success) {
                    cmdLog.setStatus(DeviceCmdLog.STATUS_SUCCESS).setResultMsg("下发成功")
                            .setElapsed(System.currentTimeMillis() - cmdLog.getCreateTime().getTime());

                    if(modelApi.getFuncType() != FuncType.W && result.getValue() != null) {
                        DataSupplier dataSupplier = supplier.getDataSupplier(result.getProtocol(), finalInvokeParam, result.getValue());
                        CollectListenerManager.getInstance().publish(dataSupplier.build(new Date()), device, RealtimePayload.TYPE_DATA_CMD);
                    }

                    IotLogger.info(IotLogger.NAME, "指令调用", uid
                            , "指令下发成功 指令: {} - 设备: {}", modelApi.getName(), device.buildDeviceKey());
                } else {
                    cmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg(result.getReason())
                            .setElapsed(System.currentTimeMillis() - cmdLog.getCreateTime().getTime());
                    IotLogger.warn(IotLogger.NAME, "指令调用", uid, "指令下发失败({}) 指令: {} - 设备: {}"
                            , result.getReason(), modelApi.getName(), device.buildDeviceKey());
                }

                IotEvenPublisher.publish(IotEventType.DeviceCmdLog, cmdLog);
                value.getCallback().accept(result);
            });

        } catch (ServiceException e) {
            cmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg(e.getMessage())
                    .setElapsed(System.currentTimeMillis() - cmdLog.getCreateTime().getTime());
            value.getCallback().accept(InvokeResult.fail(e.getMessage(), null));

            IotEvenPublisher.publish(IotEventType.DeviceCmdLog, cmdLog);
            IotLogger.error(IotLogger.NAME, "指令调用", invokeParam != null ? invokeParam.getUid() : null, e.getMessage());
        } catch (Exception e) {
            cmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg("未知异常")
                    .setElapsed(System.currentTimeMillis() - cmdLog.getCreateTime().getTime());
            value.getCallback().accept(InvokeResult.fail("未知异常", null));

            IotEvenPublisher.publish(IotEventType.DeviceCmdLog, cmdLog);
            IotLogger.error(IotLogger.NAME, "指令调用", invokeParam != null ? invokeParam.getUid() : null, e.getMessage());
        }
    }
}
