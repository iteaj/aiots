package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.iboot.module.iot.collect.CollectDataListener;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;

public interface EventGroupCollectListener extends CollectDataListener {

    default void listener(CollectInfo info) {}

    void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info);
}
