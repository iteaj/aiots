package com.iteaj.framework.spi.iot.data;

import java.util.Collection;

/**
 * 模型属性实时数据服务主要用于：
 * 1. 获取设备的实时数据
 * 2. 获取设备的实时状态
 */
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
     * 获取指定分组下面的设备列表
     * @param groupId 分组ID
     * @return 设备列表
     */
    Collection<? extends RealtimeDeviceStatus> listByGroupId(Long groupId);

    /**
     * 获取指定产品下面的设备列表
     * @param productCode 产品码
     * @return 设备列表
     */
    Collection<? extends RealtimeDeviceStatus> listByProductCode(String productCode);

    /**
     * 根据协议码和设备编号获取设备信息
     * @param protocolCode 协议码
     * @param deviceSn 设备编号
     * @return {@link  RealtimeDeviceStatus}
     */
    RealtimeDeviceStatus getDeviceStatus(String protocolCode, String deviceSn);

    /**
     *  获取指定的网关子设备信息
     * @param protocolCode 协议码
     * @param deviceSn 设备编号(子设备编号)
     * @param parentDeviceSn 父设备编号(网关设备编号)
     * @return {@link  RealtimeDeviceStatus}
     */
    RealtimeDeviceStatus getDeviceStatus(String protocolCode, String deviceSn, String parentDeviceSn);
}
