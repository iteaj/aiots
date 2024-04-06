package com.iteaj.iboot.module.iot.cache.data;

import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;

public class RealtimeDataCacheListener implements EventGroupCollectListener, SignalCollectListener {

    private final RealtimeDataService realtimeDataService;

    public RealtimeDataCacheListener(RealtimeDataService realtimeDataService) {
        this.realtimeDataService = realtimeDataService;
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {
        realtimeDataService.put(info.getSupplier());
    }

    @Override
    public void supplier(CollectDevice device, CollectData data) {
        realtimeDataService.put(device.getProtocolCode(), device.buildKey()
                , SignalOrFieldValue.build(data.getAddress(), data.getCollectTime()
                        , data.getValue(), data.getAddress()).builder(data.getCollectStatus()));
    }
}
