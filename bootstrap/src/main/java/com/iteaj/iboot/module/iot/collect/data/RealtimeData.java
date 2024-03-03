package com.iteaj.iboot.module.iot.collect.data;

import lombok.Data;

/**
 * 实时数据
 */
@Data
public class RealtimeData<T> {

    /**
     * 上一条记录
     */
    private T prev;

    /**
     * 实时数据
     */
    private T realtime;

    /**
     * 事件码或者信号字段
     */
    private String eventOrSignalField;

    public RealtimeData(String eventOrSignalField, T realtime) {
        this.realtime = realtime;
        this.eventOrSignalField = eventOrSignalField;
    }

    public void setRealtime(T realtime) {
        this.prev = this.realtime;
        this.realtime = realtime;
    }
}
