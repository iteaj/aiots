package com.iteaj.iboot.module.iot.collect;

import com.iteaj.iboot.module.iot.entity.CollectData;

import java.util.List;

public interface SignalCollectListener extends CollectDataListener {

    void listener(CollectDevice device, List<CollectData> data);
}
