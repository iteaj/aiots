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

    public RealtimeData(T realtime) {
        this.realtime = realtime;
    }

    public void setRealtime(T realtime) {
        this.prev = this.realtime;
        this.realtime = realtime;
    }
}
