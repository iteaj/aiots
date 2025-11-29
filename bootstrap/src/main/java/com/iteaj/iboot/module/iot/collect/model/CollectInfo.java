package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iot.utils.UniqueIdGen;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CollectInfo {

    private String cid;

    /**
     * 此次采集的事件源
     */
    private EventSource source;

    /**
     * 设备组
     */
    private DeviceGroup group;

    /**
     * 采集时间
     * @see DataSupplier#getCollectTime()
     * @see DataSupplier#build(Date)
     */
    private Date collectTime;

    /**
     * 设备信息
     */
    private List<DeviceInfo> infos = new ArrayList<>();

    public CollectInfo(EventSource source, DeviceGroup group) {
        this.group = group;
        this.source = source;
        this.collectTime = new Date();
        this.cid = String.valueOf(UniqueIdGen.nextLong());
    }

    public DeviceInfo addDeviceInfo(RealtimeStatus device, ModelApi api, DataSupplier supplier) {
        DeviceInfo deviceInfo = new DeviceInfo(api, device, supplier.build(this.collectTime));
        this.infos.add(deviceInfo); return deviceInfo;
    }

    @Getter
    @Setter
    public class DeviceInfo {

        private ModelApi api;

        private RealtimeStatus device;

        private DataSupplier supplier;

        public DeviceInfo(ModelApi api, RealtimeStatus device, DataSupplier supplier) {
            this.api = api;
            this.device = device;
            this.supplier = supplier;
        }

        public String getCid() {
            return CollectInfo.this.cid;
        }
    }
}
