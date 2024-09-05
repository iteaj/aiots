package com.iteaj.framework.spi.iot.data;

public interface IModelAttrRealtimeDataService {

    /**
     *
     * @param uid 设备uid
     * @param signalOrField 物模型属性
     * @return 实时采集数据
     */
    ModelAttrRealtimeData getRealtimeData(String uid, String signalOrField);
}
