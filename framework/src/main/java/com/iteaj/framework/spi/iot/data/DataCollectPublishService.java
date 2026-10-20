package com.iteaj.framework.spi.iot.data;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;

public interface DataCollectPublishService {

    /**
     * 发布数据
     * @param supplier
     * @param device
     */
    void publish(DataSupplier supplier, RealtimeDeviceStatus device);

    /**
     * 发布数据
     * @param supplier 数据
     * @param device 设备
     * @param type 类型 {@link RealtimePayload#getType()}
     */
    void publish(DataSupplier supplier, RealtimeDeviceStatus device, String type);
}
