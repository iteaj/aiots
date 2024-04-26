package com.iteaj.iboot.module.iot.mq;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.iteaj.framework.json.JacksonUtils;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.DeviceStatusListener;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.linkage.LinkageActionListener;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class RabbitmqDataListener implements SignalCollectListener, EventGroupCollectListener
        , LinkageActionListener, DeviceStatusListener {

    private final AmqpTemplate amqpTemplate;
    // exchange
    public final static String IOT_EXCHANGE = "iot-exchange";

    // 实时数据队列和key
    public final static String IOT_QUEUE_REALTIME_DATA = "iot.queue.realtime.data";
    public final static String IOT_ROUTING_REALTIME_DATA = "iot.routing.realtime.data";

    // 实时告警数据队列和key
    public final static String IOT_QUEUE_REALTIME_WARN = "iot.queue.realtime.warn";
    public final static String IOT_ROUTING_REALTIME_WARN = "iot.routing.realtime.warn";

    // 实时设备状态队列和key
    public final static String IOT_QUEUE_REALTIME_STATUS = "iot.queue.realtime.status";
    public final static String IOT_ROUTING_REALTIME_STATUS = "iot.routing.realtime.status";

    public RabbitmqDataListener(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public void supplier(CollectDevice device, CollectData data) {
        this.finished(device, Arrays.asList(data));
    }

    @Override
    public void finished(CollectDevice device, List<CollectData> data) {
        if(CollectionUtil.isNotEmpty(data)) {
            data.stream().filter(item -> item.getStatus()).forEach(item -> {
                try {
                    RealtimeData realtimeData = new RealtimeData(item.getSignalId(), item.getUid().toString()
                            , device.getDeviceSn(), item.getValue(), item.getAddress(), item.getCollectTime());
                    byte[] bytes = JacksonUtils.getGenericMapper().writeValueAsBytes(realtimeData);
                    amqpTemplate.send(IOT_EXCHANGE, IOT_ROUTING_REALTIME_DATA, new Message(bytes));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {
        List<SignalOrFieldValue> values = info.getSupplier().getValues();
        if(CollectionUtil.isNotEmpty(values)) {
            values.stream().filter(item -> item.getStatus() != null && item.getStatus() == CollectStatus.Success).forEach(item -> {
                try {
                    RealtimeData realtimeData = new RealtimeData(item.getId(), info.getDevice().getUid(), info.getDevice().getDeviceSn()
                            , item.getValue().toString(), item.getSignalOrField(), item.getCollectTime());
                    byte[] bytes = JacksonUtils.getGenericMapper().writeValueAsBytes(realtimeData);
                    amqpTemplate.send(IOT_EXCHANGE, IOT_ROUTING_REALTIME_DATA, new Message(bytes));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void warnTrigger(WarnConfig config, WarnRecord record, RealtimeStatus status) {
        try {
            WarnData warnData = new WarnData(record.getUid(), record.getContent()
                    , record.getDeviceSn(), record.getWarnConfigId(), record.getWarnTime());
            byte[] bytes = JacksonUtils.getGenericMapper().writeValueAsBytes(warnData);
            amqpTemplate.send(IOT_EXCHANGE, IOT_ROUTING_REALTIME_WARN, new Message(bytes));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supplier(Collection<RealtimeStatus> devices) {
        if(CollectionUtil.isNotEmpty(devices)) {
            devices.forEach(device -> {
                try {
                    byte[] bytes = JacksonUtils.getGenericMapper().writeValueAsBytes(device);
                    amqpTemplate.send(IOT_EXCHANGE, IOT_ROUTING_REALTIME_STATUS, new Message(bytes));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Getter
    @Setter
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
         * 属性或者点位地址
         */
        private String attrOrAddress;

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

    @Getter
    @Setter
    public class WarnData {
        /**
         * 告警设备
         */
        private String uid;

        /**
         * 告警内容
         */
        private String content;

        /**
         * 设备编号
         */
        private String deviceSn;

        /**
         * 所属告警配置id
         */
        private Long warnConfigId;

        /**
         * 告警事件
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date warnTime;

        public WarnData(String uid, String content, String deviceSn, Long warnConfigId, Date warnTime) {
            this.uid = uid;
            this.content = content;
            this.deviceSn = deviceSn;
            this.warnConfigId = warnConfigId;
            this.warnTime = warnTime;
        }
    }
}
