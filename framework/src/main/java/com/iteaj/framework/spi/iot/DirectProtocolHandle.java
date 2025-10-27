package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.ProtocolHandle;

import java.util.List;

/**
 * 直连协议的handle
 * @param <T>
 */
public interface DirectProtocolHandle<T extends Protocol> extends ProtocolHandle<T> {

    String LOGGER_TYPE = "数据采集";
    String LOGGER_SUB_TYPE = "直连上报";

    ModelAttrMetaService getModelAttrMetaService();

    IModelAttrRealtimeDataService getRealtimeDataService();

    DataCollectPublishService getDataCollectPublishService();

    @Override
    default Object handle(T protocol) {
        Object result = doHandle(protocol);
        String equipCode = protocol.getEquipCode();
        try {
            DeviceProtocolSupplier protocolSupplier = ProtocolSupplierManager.get(protocol.requestMessage().getClass());
            RealtimeDeviceStatus deviceStatus = getRealtimeDataService().getDeviceStatus(protocolSupplier.getProtocol().getCode(), equipCode);
            if(deviceStatus != null) {
                List<ModelAttrMeta> attrMetas = getModelAttrMetaService().getAttrMetas(deviceStatus.getProductCode());
                ProtocolModelApiInvokeParam param = new ProtocolModelApiInvokeParam(deviceStatus.getUid())
                        .setDeviceSn(deviceStatus.getDeviceSn()).addDevice(deviceStatus);
                DataSupplier dataSupplier = protocolSupplier.getDataSupplier(protocol, param, attrMetas);
                if(dataSupplier != null) {
                    getDataCollectPublishService().publish(dataSupplier, deviceStatus);
                }
            }
        } catch (Exception e) {
            DeviceLogger.error(LOGGER_TYPE, LOGGER_SUB_TYPE, equipCode, "直连设备数据上报解析异常", e);
        }

        return result;
    }

    Object doHandle(T protocol);
}
