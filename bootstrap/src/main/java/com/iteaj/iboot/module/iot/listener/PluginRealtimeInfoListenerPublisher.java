package com.iteaj.iboot.module.iot.listener;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
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
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.linkage.LinkageActionListener;
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
        , DeviceStatusListener, ModelAttrListener, LinkageActionListener {

    @Override
    public void supplier(CollectDevice device, CollectData data) {
        RealtimePayload payload = new RealtimePayload("data", Arrays.asList(new RealtimeData(data.getSignalId()
                , device.getUid() + "", device.getDeviceSn(), data.getValue(), data.getField(), data.getAddress()
                , data.getCollectTime()).setCollectMode("signal")
                .setStatus(data.getStatus()).setReason(data.getReason())
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
        doPublisher(source.getId(), info.getSupplier(), info.getDevice());
    }

    private void doPublisher(Long taskId, DataSupplier supplier, RealtimeStatus device) {
        List<RealtimeData> collect = supplier.getValues().stream()
                .map(item -> new RealtimeData(item.getId(), device.getUid()
                        , device.getDeviceSn(), item.getValue(), item.getSignalOrField()
                        , item.getAddress(), item.getCollectTime())
                        .setCollectMode("model").setTaskId(taskId)
                        .setDeviceGroupId(device.getGroupId())
                        .setProductCode(device.getProductCode())
                        .setProtocolCode(device.getProtocolCode())
                        .buildFromStatus(item.getStatus())
                ).collect(Collectors.toList());

        IotEvenPublisher.publish(IotEventType.Realtime, new RealtimePayload("data", collect));
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device) {
        doPublisher(null, supplier, device);
    }

    /**
     * todo 告警触发处理
     * @param config
     * @param record
     * @param status
     */
    @Override
    public void warnTrigger(WarnConfig config, WarnRecord record, RealtimeStatus status) {
//        LinkageActionListener.super.warnTrigger(config, record, status);
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public class RealtimeData implements RealtimeCollectData {

        /**
         * 点位id或者模型属性id
         */
        private Long fieldId;

        /**
         * 设备uid
         */
        private String uid;

        /**
         * 采集状态
         */
        private Boolean status;

        /**
         * 失败原因
         */
        private String reason;

        /**
         * cid
         */
        private String cid;

        /**
         * 设备编号
         */
        private String deviceSn;

        /**
         * 采集值
         */
        private Object value;

        /**
         * 任务id
         */
        private Long taskId;

        /**
         * 采集模式
         */
        private String collectMode;

        /**
         * 字段
         */
        private String field;

        /**
         * 点位地址
         */
        private String address;

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
        private Date collectTime;

        public RealtimeData(Long fieldId, String uid, String deviceSn, Object value, String field, String address, Date collectTime) {
            this.fieldId = fieldId;
            this.uid = uid;
            this.address = address;
            this.deviceSn = deviceSn;
            this.value = value;
            this.field = field;
            this.collectTime = collectTime;
        }

        public RealtimeData buildFromStatus(CollectStatus status) {
            if(status == CollectStatus.Success) {
                this.status = true;
            } else {
                this.status = false;
            }

            this.reason = status.getReason();
            return this;
        }
    }
}
