package com.iteaj.iboot.plugin.datasource.tdengine.listener;

import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iot.taos.TaosSqlManager;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TaosRealtimeDataListener implements RealtimeInfoListener {

    private final TaosSqlManager taosSqlManager;

    public TaosRealtimeDataListener(TaosSqlManager taosSqlManager) {
        this.taosSqlManager = taosSqlManager;
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        if(Objects.equals(value.getType(), "data_full")) {
            AtomicLong timeMillis = new AtomicLong(System.currentTimeMillis());
            List collect = (List) value.getPayload().stream().map(item -> {
                RealtimeCollectData data = (RealtimeCollectData) item;
                String collectValue = data.getValue() != null ? data.getValue().toString() : null;
                return new TaosRealtimeData().setAddress(data.getAddress())
                        .setTaskId(data.getTaskId()).setField(data.getField())
                        .setCollectTime(data.getCollectTime()).setCreateTime(timeMillis.getAndIncrement())
                        .setStatus(data.getStatus()).setValue(collectValue)
                        .setDeviceSn(data.getDeviceSn()).setDeviceGroupId(data.getDeviceGroupId())
                        .setDeviceName(data.getDeviceName()).setFieldName(data.getFieldName())
                        .setReason(data.getReason()).setCollectMode(data.getCollectMode())
                        .setProductCode(data.getProductCode()).setProtocolCode(data.getProtocolCode())
                        .setCid(data.getCid()).setUid(data.getUid()).setFieldId(data.getFieldId());
            }).collect(Collectors.toList());

            taosSqlManager.batchInsert(TaosRealtimeData.class, collect);
        }
    }
}
