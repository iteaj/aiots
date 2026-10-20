package com.iteaj.iboot.plugin.datasource.tdengine.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.framework.spi.iot.listener.filter.RealtimeListenerFilter;
import com.iteaj.iot.taos.TaosSqlManager;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TaosRealtimeDataListener implements RealtimeListenerFilter {

    private final TaosSqlManager taosSqlManager;

    public TaosRealtimeDataListener(TaosSqlManager taosSqlManager) {
        this.taosSqlManager = taosSqlManager;
    }

    @Override
    public void doIotEvent(IotEventType eventType, RealtimePayload value) {
        if(isFilter(value.getType(), value)) {
            if(CollectionUtil.isNotEmpty(value.getPayload())) {
                AtomicLong timeMillis = new AtomicLong(System.currentTimeMillis());
                List collect = (List) value.getPayload().stream().map(item -> {
                    RealtimeCollectData data = (RealtimeCollectData) item;
                    return TaosRealtimeData.of(data, timeMillis.getAndIncrement());
                }).collect(Collectors.toList());

                taosSqlManager.batchInsert(TaosRealtimeData.class, collect);
            }
        }
    }

    @Override
    public String getFilterName() {
        return FILTER_DATABASE;
    }

    @Override
    public boolean isFilter(String type, RealtimePayload payload) {
        return payload.isDataFull() || payload.isDataActive();
    }
}
