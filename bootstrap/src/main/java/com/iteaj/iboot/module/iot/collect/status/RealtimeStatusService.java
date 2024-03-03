package com.iteaj.iboot.module.iot.collect.status;

import java.util.Map;

public interface RealtimeStatusService {

    /**
     * 获取指定设备的实时状态
     * @param protocolCode
     * @param deviceSn
     * @return
     */
    RealtimeStatus get(String protocolCode, String deviceSn);

    /**
     * 获取指定设备列表的实时状态
     * @param protocolCode
     * @param deviceSn
     * @return
     */
    Map<String, RealtimeStatus> get(String protocolCode, String... deviceSn);

    /**
     * 缓存状态
     * @param protocolCode 产品代码
     * @param deviceSn 直连设备或者边缘网关设备的设备编号(或网关设备编号和网关子设备编号的组合)
     * @param value
     */
    void put(String protocolCode, String deviceSn, RealtimeStatus value);

    /**
     * 移除指定设备的状态
     * @param protocolCode
     * @param deviceSn
     * @return
     */
    RealtimeStatus remove(String protocolCode, String deviceSn);

    /**
     * 移除指定设备列表的实时状态
     * @param protocolCode
     * @param deviceSn
     * @return
     */
    Map<String, RealtimeStatus> remove(String protocolCode, String... deviceSn);
}
