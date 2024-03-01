package com.iteaj.iboot.module.iot.collect.data;

import java.util.List;
import java.util.Map;

/**
 * 实时数据服务
 */
public interface RealtimeDataService {

    /**
     * 移除指定产品下得所有数据
     * @param productCode
     */
    void remove(String productCode);

    /**
     * 移除指定产品下指定设备得所有信息
     * @param productCode
     * @param deviceSn
     */
    Map<String, RealtimeData> remove(String productCode, String deviceSn);

    /**
     * 移除指定产品下指定设备下得事件或者点位字段信息
     * @param productCode
     * @param deviceSn
     * @param eventOrSignalField
     */
    Map<String, RealtimeData> remove(String productCode, String deviceSn, String eventOrSignalField);

    /**
     * 缓存数据
     * @param productCode 产品代码
     * @param deviceSn 设备编号
     * @param eventOrSignalField 事件代码或者点位字段
     * @param value
     */
    void put(String productCode, String deviceSn, String eventOrSignalField, Object value);

    /**
     * 获取指定设备下面的所有实时数据
     * @param productCode
     * @param deviceSn 设备编号
     * @return
     */
    Map<String, RealtimeData> listOfDevice(String productCode, String deviceSn);

    /**
     * 获取指定设备下指定事件的实时数据
     * @param productCode
     * @param deviceSn 设备编号
     * @param eventOrSignalField 事件代码或者点位字段
     * @return
     */
    RealtimeData getOfDeviceAndKey(String productCode, String deviceSn, String eventOrSignalField);

}
