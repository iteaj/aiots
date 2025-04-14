package com.iteaj.framework.spi.iot.data;

import com.iteaj.framework.spi.iot.SignalOrFieldValue;

public interface ModelAttrRealtimeData {

    /**
     * 上一条采集记录
     */
    SignalOrFieldValue getPrev();

    /**
     * 改变前的记录
     * @return
     */
    SignalOrFieldValue getChange();

    /**
     * 最新采集记录
     */
    SignalOrFieldValue getRealtime();
}
