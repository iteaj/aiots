package com.iteaj.iboot.module.iot.cache.data;

import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
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
     * 值改变前的记录
     */
    private SignalOrFieldValue change;

    /**
     * 点位地址或者字段名
     */
    private String signalOrField;

    public RealtimeData( SignalOrFieldValue realtime) {
        this.realtime = realtime;
        this.signalOrField = realtime.getSignalOrField();
    }

    public void setRealtime(SignalOrFieldValue realtime) {
        // 上一条记录必须是成功状态
        if(this.realtime.getStatus() == CollectStatus.Success) {
            this.prev = this.getRealtime();
            if(realtime.getStatus() == CollectStatus.Success) {
                if(!Objects.equals(realtime.getValue(), this.getRealtime().getValue())) {
                    this.change = this.getRealtime();
                } else if(this.getChange() == null) { // 第二次数据如果还是没有改变需要将change改成最新的记录
                    this.change = this.getRealtime();
                }
            } else {
                this.change = this.getRealtime();
            }
        } else {
            this.change = this.getPrev();
        }

        this.realtime = realtime;
    }
}
