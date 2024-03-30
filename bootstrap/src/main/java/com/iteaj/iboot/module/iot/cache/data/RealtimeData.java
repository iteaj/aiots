package com.iteaj.iboot.module.iot.cache.data;

import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import lombok.Data;

/**
 * 实时数据
 */
@Data
public class RealtimeData {

    /**
     * 上一条记录
     */
    private SignalOrFieldValue prev;

    /**
     * 实时数据
     */
    private SignalOrFieldValue realtime;

    /**
     * 点位地址或者字段名
     */
    private String signalOrField;

    public RealtimeData( SignalOrFieldValue realtime) {
        this.realtime = realtime;
        this.signalOrField = realtime.getSignalOrField();
    }

    public void setRealtime(SignalOrFieldValue realtime) {
        this.prev = this.realtime;
        this.realtime = realtime;
    }
}
