package com.iteaj.iboot.module.iot.listener;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.framework.spi.iot.IotUtil;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.ScriptInvokeListener;
import com.iteaj.framework.spi.iot.listener.ScriptInvokePayload;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.iboot.module.core.entity.Admin;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.consts.WarnGrade;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.service.IWarnRecordService;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 设备指令控制监听实现
 */
@Component
public class DeviceCtrlEventListener implements ScriptInvokeListener {

    private final IotCacheManager cacheManager;
    private final IProductService productService;
    private final IModelApiService modelApiService;
    private final IWarnRecordService warnRecordService;
    public DeviceCtrlEventListener(IotCacheManager cacheManager
            , IProductService productService, IModelApiService modelApiService, IWarnRecordService warnRecordService) {
        this.cacheManager = cacheManager;
        this.productService = productService;
        this.modelApiService = modelApiService;
        this.warnRecordService = warnRecordService;
    }

    @Override
    public void onIotEvent(IotEventType eventType, ScriptInvokePayload value) {
        if(value.isCmd()) {
            DeviceCmdLog deviceCmdLog = new DeviceCmdLog().setMethod(DeviceCmdLog.METHOD_SCRIPT)
                    .setOperator("脚本").setCreateTime(new Date());
            try {

                final ProtocolModelApiInvokeParam invokeParam = IotUtil.createParamByCmdCode(value.getCmd(), value.getUid());
                deviceCmdLog.setUid(invokeParam.getUid()).putCmdParams(invokeParam.getParam()).putCmdName(value.getCmd());

                //查找对应的物模型api
                ModelApi modelApi = modelApiService.detailByCode(invokeParam.getApiCode())
                        .ifNotPresentThrow("接口代码不存在[apiCode=" + invokeParam.getApiCode() + "]")
                        .getData();
                deviceCmdLog.setCmdType(modelApi.getType().name()).putCmdName(modelApi.getName());

                RealtimeStatus device = cacheManager.getByUid(invokeParam.getUid());
                if(device == null) {
                    throw new ServiceException("设备不存在["+invokeParam.getUid()+"]");
                }
                deviceCmdLog.setDeviceName(device.getDeviceName());


                ProductDto product = this.productService.detailById(modelApi.getProductId())
                        .ifNotPresentThrow("未找到所属产品").getData();

                if(product.getStatus() == FuncStatus.disabled) {
                    throw new ServiceException("产品未启用["+product.getName()+"]");
                }

                if(device.getStatus() == DeviceStatus.offline) {
                    if(device.getType() == DeviceType.Direct) {
                        throw new ServiceException("设备不在线["+device.getDeviceName()+"]");
                    } else if(device.getType() == DeviceType.Child) {
                        RealtimeStatus parent = cacheManager.get(device.getProtocolCode(), device.getParentDeviceSn());
                        if(parent != null && parent.getStatus() == DeviceStatus.offline) {
                            throw new ServiceException("网关设备不在线["+parent.getDeviceName()+"]");
                        }
                    }
                }

                ProtocolInvokeUtil.invoke(modelApi, invokeParam, device, result -> {
                    value.setResult(result);
                    if(result.getStatus() == ExecStatus.success) {
                        deviceCmdLog.setStatus(DeviceCmdLog.STATUS_SUCCESS).setResultMsg("执行成功");
                    } else {
                        deviceCmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg(result.getReason());
                    }

                    deviceCmdLog.setElapsed(System.currentTimeMillis() - deviceCmdLog.getCreateTime().getTime());
                    IotEvenPublisher.publish(IotEventType.DeviceCmdLog, deviceCmdLog);
                });
            } catch (ServiceException e) {
                deviceCmdLog.setStatus(DeviceCmdLog.STATUS_FAILURE).setResultMsg(e.getMessage())
                        .setElapsed(System.currentTimeMillis() - deviceCmdLog.getCreateTime().getTime());;
                IotEvenPublisher.publish(IotEventType.DeviceCmdLog, deviceCmdLog);
                throw e;
            }
        } else if (value.isWarn()) {
            WarnData data = value.getData();
            WarnRecord warnRecord = new WarnRecord().setWarnTime(data.getWarnTime())
                    .setUid(data.getUid()).setProductCode(data.getProductCode()).setValue(data.getValue())
                    .setDeviceSn(data.getDeviceSn()).setCreateTime(new Date()).setWarnConfigId(data.getWarnConfigId())
                    .setContent(data.getContent()).setField(data.getField());
            warnRecordService.save(warnRecord);
        }
    }
}
