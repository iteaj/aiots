package com.iteaj.iboot.module.iot.collect.data;

import java.util.Map;

/**
 * 实时数据服务
 */
public interface RealtimeDataService {

    /**
     * 移除指定协议下得所有数据
     * @param protocolCode
     */
    void remove(String protocolCode);

    /**
     * 移除指定协议下指定设备得所有信息
     * @param protocolCode
     * @param deviceSn
     */
    Map<String, RealtimeData> remove(String protocolCode, String deviceSn);

    /**
     * 移除指定协议下指定设备下得事件或者点位字段信息
     * @param protocolCode
     * @param deviceSn
     * @param eventOrSignalField
     */
    RealtimeData remove(String protocolCode, String deviceSn, String eventOrSignalField);


    /**
     * 移除指定协议下指定设备下得事件或者点位字段信息
     * @param protocolCode
     * @param deviceSn
     * @param eventOrSignalFields
     */
    Map<String, RealtimeData> remove(String protocolCode, String deviceSn, String... eventOrSignalFields);

    /**
     * 缓存数据
     * @param protocolCode 产品代码
     * @param deviceSn 直连设备或者边缘网关设备的设备编号(或网关设备编号和网关子设备编号的组合)
     * @param eventOrSignalField 事件代码或者点位字段
     * @param value
     */
    void put(String protocolCode, String deviceSn, String eventOrSignalField, Object value);

    /**
     * 获取指定设备下面的所有实时数据
     * @param protocolCode
     * @param deviceSn 设备编号
     * @return
     */
    Map<String, RealtimeData> listOfDevice(String protocolCode, String deviceSn);

    /**
     * 获取指定设备下指定事件的实时数据
     * @param protocolCode
     * @param deviceSn 设备编号
     * @param eventOrSignalField 事件代码或者点位字段
     * @return
     */
    RealtimeData getOfDeviceAndKey(String protocolCode, String deviceSn, String eventOrSignalField);

    /**
     * 获取指定设备下面的所有实时数据
     * @param protocolCode
     * @param deviceSn 设备编号
     * @param eventOrSignalFields 事件代码或者点位字段列表
     * @return
     */
    Map<String, RealtimeData> listOfDevice(String protocolCode, String deviceSn, String... eventOrSignalFields);

}
