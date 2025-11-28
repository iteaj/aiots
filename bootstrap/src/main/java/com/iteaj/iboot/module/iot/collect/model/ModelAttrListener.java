package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDataListener;

public interface ModelAttrListener extends CollectDataListener {

    /**
     * 模型属性发布
     * @param supplier
     * @param device
     */
    void supplier(DataSupplier supplier, RealtimeStatus device);

    /**
     * 模型属性发布
     * @param supplier 数据
     * @param device 设备
     * @param type 发布类型 {@link RealtimePayload#getType()}
     */
    default void supplier(DataSupplier supplier, RealtimeStatus device, String type) {
        supplier(supplier, device);
    }
}
