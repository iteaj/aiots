package com.iteaj.iboot.module.iot.cache.data;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.collect.model.ModelAttrListener;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import org.springframework.core.Ordered;

public class RealtimeDataCacheListener implements EventGroupCollectListener, ModelAttrListener {

    private final RealtimeDataService realtimeDataService;

    public RealtimeDataCacheListener(RealtimeDataService realtimeDataService) {
        this.realtimeDataService = realtimeDataService;
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device) {
        if(supplier != null) {
            realtimeDataService.put(supplier);
            IotLogger.debug(IotLogger.REPORT_NAME, supplier.getProtocolCode(), device.getUid()
                    , "<<< 接收到设备上报数据 设备编号: {}", supplier.getKey().getKey());
        }
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {
        realtimeDataService.put(info.getSupplier());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE + 518;
    }
}
