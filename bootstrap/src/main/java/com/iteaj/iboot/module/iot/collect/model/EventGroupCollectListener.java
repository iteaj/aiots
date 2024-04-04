package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.iboot.module.iot.collect.CollectDataListener;

public interface EventGroupCollectListener extends CollectDataListener {

    void listener(CollectInfo info);
}
