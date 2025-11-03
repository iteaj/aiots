package com.iteaj.framework.spi.iot.data;

public interface IModelAttrRealtimeDataService {

    /**
     *
     * @param uid 设备uid
     * @param signalOrField 物模型属性
     * @return 实时采集数据
     */
    ModelAttrRealtimeData getRealtimeData(String uid, String signalOrField);

    /**
     * 获取设备的实时状态
     * @param uid 设备uid
     * @return 设备实时状态数据
     */
    RealtimeDeviceStatus getDeviceStatus(String uid);

    /**
     * 根据协议码和设备编号获取设备信息
     * @param protocolCode
     * @param deviceSn
     * @return
     */
    RealtimeDeviceStatus getDeviceStatus(String protocolCode, String deviceSn);

    /**
     *  获取指定的网关子设备信息
     * @param protocolCode
     * @param deviceSn 设备编号
     * @param parentDeviceSn 父设备编号(网关设备编号)
     * @return {@link  RealtimeDeviceStatus}
     */
    RealtimeDeviceStatus getDeviceStatus(String protocolCode, String deviceSn, String parentDeviceSn);
}
