package com.iteaj.iboot.module.iot.listener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.DeviceStatusListener;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.collect.model.ModelAttrListener;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 将采集的数据、设备的实时状态、上报的模型属性数据发布出去, 用于给其他插件模块使用
 */
public class PluginRealtimeInfoListenerPublisher implements SignalCollectListener, EventGroupCollectListener
        , DeviceStatusListener, ModelAttrListener {

    @Override
    public void supplier(CollectDevice device, CollectData data) {
        RealtimePayload payload = new RealtimePayload("data", Arrays.asList(new RealtimeData(data.getSignalId()
                , device.getUid() + "", device.getDeviceSn(), data.getValue(), data.getAddress(), data.getCollectTime())
                .setTaskId(data.getCollectTaskId())
                .setProductCode(device.getProductCode())
                .setProtocolCode(device.getProtocolCode())));
        IotEvenPublisher.publish(IotEventType.Realtime, payload);
    }

    @Override
    public void supplier(Collection<RealtimeStatus> devices) {
        IotEvenPublisher.publish(IotEventType.Realtime, new RealtimePayload("status", devices));
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {
        this.supplier(info.getSupplier(), info.getDevice());
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device) {
        List<RealtimeData> collect = supplier.getValues().stream()
                .map(item -> new RealtimeData(item.getId(), device.getUid()
                    , device.getDeviceSn(), item.getValue() == null ? null : item.getValue().toString()
                    , item.getSignalOrField(), item.getCollectTime())
                    .setDeviceGroupId(device.getGroupId())
                    .setProductCode(device.getProductCode())
                    .setProtocolCode(device.getProtocolCode())
                ).collect(Collectors.toList());

        RealtimePayload payload = new RealtimePayload("data", collect);
        IotEvenPublisher.publish(IotEventType.Realtime, payload);
    }


    @Getter
    @Setter
    @Accessors(chain = true)
    public class RealtimeData {

        /**
         * 点位id或者模型属性id
         */
        private Long id;

        /**
         * 设备uid
         */
        private String uid;

        /**
         * 设备编号
         */
        private String deviceSn;

        /**
         * 采集值
         */
        private String value;

        /**
         * 任务id
         */
        private Long taskId;

        /**
         * 属性或者点位地址
         */
        private String attrOrAddress;

        /**
         * 设备组id
         */
        private Long deviceGroupId;

        /**
         * 产品码
         */
        private String productCode;

        /**
         * 协议码
         */
        private String protocolCode;

        /**
         * 采集时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date collectTime;

        public RealtimeData(Long id, String uid, String deviceSn, String value, String attrOrAddress, Date collectTime) {
            this.id = id;
            this.uid = uid;
            this.deviceSn = deviceSn;
            this.value = value;
            this.attrOrAddress = attrOrAddress;
            this.collectTime = collectTime;
        }
    }
}
