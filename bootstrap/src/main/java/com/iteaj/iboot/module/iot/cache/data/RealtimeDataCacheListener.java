package com.iteaj.iboot.module.iot.cache.data;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.DeviceKey;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.entity.CollectData;

import java.util.List;

public class RealtimeDataCacheListener implements EventGroupCollectListener, SignalCollectListener {

    private final RealtimeDataService realtimeDataService;

    public RealtimeDataCacheListener(RealtimeDataService realtimeDataService) {
        this.realtimeDataService = realtimeDataService;
    }

    @Override
    public void listener(CollectInfo info) {
        if(CollectionUtil.isNotEmpty(info.getInfos())) {
            info.getInfos().forEach(deviceInfo -> {
                realtimeDataService.put(deviceInfo.getSupplier());
            });
        }
    }

    @Override
    public void listener(CollectDevice device, List<CollectData> data) {
        if(data != null) {
            DeviceKey deviceKey = device.buildKey();
            data.forEach(item -> {
                realtimeDataService.put(device.getProtocolCode(), deviceKey
                        , SignalOrFieldValue.build(item.getAddress(), item.getAddress()
                                , item.getCollectTime(), item.getValue()));
            });
        }
    }
}
