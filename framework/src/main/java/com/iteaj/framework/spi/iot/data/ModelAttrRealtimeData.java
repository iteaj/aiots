package com.iteaj.framework.spi.iot.data;

import com.iteaj.framework.spi.iot.SignalOrFieldValue;

public interface ModelAttrRealtimeData {

    /**
     * 模型属性的扩展信息
     */
    RealtimeDataExtra getExtra();

    /**
     * 上一条采集记录
     */
    SignalOrFieldValue getPrev();

    /**
     * 最新采集记录
     */
    SignalOrFieldValue getRealtime();
}
