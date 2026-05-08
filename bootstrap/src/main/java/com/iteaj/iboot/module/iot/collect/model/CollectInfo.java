package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.dto.EventSourceDto;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iot.consts.ExecStatus;
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
    private EventSourceDto source;

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

    public CollectInfo(EventSourceDto source) {
        this.source = source;
        this.collectTime = new Date();
        this.cid = String.valueOf(UniqueIdGen.nextLong());
    }

    public DeviceInfo addDeviceInfo(RealtimeStatus device) {
        return addDeviceInfo(device, null, new DataSupplier(device.getProtocolCode(), device.buildKey()));
    }

    public DeviceInfo addDeviceInfo(RealtimeStatus device, ModelApi api, DataSupplier supplier) {
        DeviceInfo deviceInfo = new DeviceInfo(api, device, supplier.build(this.collectTime));
        this.infos.add(deviceInfo); return deviceInfo;
    }

    @Getter
    @Setter
    public class DeviceInfo {

        private ModelApi api;

        private ExecStatus status;

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

        /**
         * 合并接口数据
         * @param data 要合并数据提供者
         * @return 当前设备信息
         */
        public DeviceInfo merge(DataSupplier data) {
            if(data != null && !data.getValues().isEmpty()) {
                data.getValues().forEach(item -> {
                    if(item.getCollectTime() == null) {
                        item.setCollectTime(CollectInfo.this.collectTime);
                    }

                    this.supplier.addValue(item);
                });
            }

            return this;
        }

        /**
         * 合并执行状态(有一个接口采集成功则代表成功)
         * @param status 执行状态
         * @return 当前设备信息
         */
        public DeviceInfo merge(ExecStatus status) {
            if(this.status == null) {
                this.status = status;
            }

            if(ExecStatus.success == status) {
                this.status = ExecStatus.success;
            }

            return this;
        }
    }
}
