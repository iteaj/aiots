package com.iteaj.iboot.module.iot.cache.data;

import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
import com.iteaj.framework.spi.iot.data.RealtimeDataExtra;
import lombok.Data;

import java.util.Objects;

/**
 * 实时数据
 */
@Data
public class RealtimeData implements ModelAttrRealtimeData {

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

    /**
     * 扩展信息
     */
    private RealtimeDataExtra extra = new RealtimeDataExtra();

    public RealtimeData( SignalOrFieldValue realtime) {
        this.realtime = realtime;
        this.signalOrField = realtime.getSignalOrField();
    }

    public void setRealtime(SignalOrFieldValue realtime) {
        if(realtime.getStatus() == CollectStatus.Success) {
            this.prev = this.getRealtime();
            this.realtime = realtime;
        }

    }

    @Override
    public RealtimeDataExtra getExtra() {
        return this.extra;
    }
}
