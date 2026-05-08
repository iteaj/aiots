package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.framework.thread.IBootThreadManger;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.ProtocolHandle;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 直连协议的handle
 * @param <T>
 */
public interface DirectProtocolHandle<T extends Protocol> extends ProtocolHandle<T> {

    String LOGGER_TYPE = "数据采集";
    String LOGGER_SUB_TYPE = "直连上报";

    @Override
    default Object handle(T protocol) {
        Object result = null;
        String equipCode = protocol.getEquipCode();
        try {
            result = doHandle(protocol);
            DeviceProtocolSupplier<?, ?> protocolSupplier = ProtocolSupplierManager.get(protocol.requestMessage().getClass());
            RealtimeDeviceStatus device = IotCoreApiService.getDeviceStatus(protocolSupplier.getProtocol().getCode(), equipCode);
            if(device != null) {
                Map<Serializable, ModelAttrMeta> attrMetas = IotCoreApiService.getAttrMetas(device.getProductCode());
                ProtocolModelApiInvokeParam param = new ProtocolModelApiInvokeParam(device.getUid())
                        .setDeviceSn(device.getDeviceSn()).addDevice(device);
                DataSupplier dataSupplier = protocolSupplier.getDataSupplier(protocol, param, attrMetas);
                if(dataSupplier != null && !dataSupplier.getValues().isEmpty()) {
                    IBootThreadManger.execute(() -> IotCoreApiService.publish(dataSupplier, device, RealtimePayload.TYPE_DATA_ACTIVE));
                }
            }
        } catch (Exception e) {
            DeviceLogger.error(LOGGER_TYPE, LOGGER_SUB_TYPE, equipCode, "直连设备数据上报解析异常", e);
        }

        return result;
    }

    Object doHandle(T protocol);
}
