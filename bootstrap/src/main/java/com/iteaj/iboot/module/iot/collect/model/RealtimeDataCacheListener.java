package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;

public class RealtimeDataCacheListener implements EventGroupCollectListener{

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
}
