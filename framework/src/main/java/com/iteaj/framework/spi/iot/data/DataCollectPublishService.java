package com.iteaj.framework.spi.iot.data;

import com.iteaj.framework.spi.iot.DataSupplier;

public interface DataCollectPublishService {

    void publish(DataSupplier supplier, RealtimeDeviceStatus device);
}
