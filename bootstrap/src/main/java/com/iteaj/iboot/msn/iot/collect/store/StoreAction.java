package com.iteaj.iboot.msn.iot.collect.store;

import com.iteaj.iboot.msn.iot.entity.CollectData;

import java.util.List;

public abstract class StoreAction {

    /**
     * 采集动作名称
     * @return
     */
    public abstract String getName();

    /**
     * 采集动作说明
     * @return
     */
    public abstract String getDesc();

    /**
     * 存储
     * @param data
     */
    public abstract void store(List<CollectData> data);
}
