package com.iteaj.iboot.plugin.datasource.tdengine.listener;

import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iot.taos.TaosSqlManager;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaosRealtimeDataListener implements RealtimeInfoListener {

    private final TaosSqlManager taosSqlManager;

    public TaosRealtimeDataListener(TaosSqlManager taosSqlManager) {
        this.taosSqlManager = taosSqlManager;
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        if(Objects.equals(value.getType(), "data")) {
            List collect = (List) value.getPayload().stream().map(item -> {
                RealtimeCollectData data = (RealtimeCollectData) item;
                return new TaosRealtimeData().setAddress(data.getAddress())
                        .setTaskId(data.getTaskId()).setField(data.getField())
                        .setReason(data.getReason()).setMode(data.getMode())
                        .setStatus(data.getStatus()).setValue(data.getValue())
                        .setCollectTime(data.getCollectTime()).setCreateTime(new Date())
                        .setCid(data.getCid()).setUid(data.getUid()).setFieldId(data.getFieldId());
            }).collect(Collectors.toList());

            taosSqlManager.batchInsert(TaosRealtimeData.class, collect);
        }
    }
}
