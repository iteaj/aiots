package com.iteaj.iboot.module.iot.mq;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.iteaj.framework.json.JacksonUtils;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.DeviceStatusListener;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.linkage.LinkageActionListener;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class RabbitmqDataListener implements SignalCollectListener, EventGroupCollectListener
        , LinkageActionListener, DeviceStatusListener {

    private final AmqpTemplate amqpTemplate;
    private final static String IOT_EXCHANGE = "iot-exchange";
    private final static String IOT_ROUTING_REALTIME_DATA = "iot.routing.realtime.data";
    private final static String IOT_ROUTING_REALTIME_WARN = "iot.routing.realtime.warn";
    private final static String IOT_ROUTING_REALTIME_STATUS = "iot.routing.realtime.status";

    public RabbitmqDataListener(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public void supplier(CollectDevice device, CollectData data) {
        try {
            byte[] bytes = JacksonUtils.getGenericMapper().writeValueAsBytes(data);
            amqpTemplate.send(IOT_EXCHANGE, IOT_ROUTING_REALTIME_DATA, new Message(bytes));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finished(CollectDevice device, List<CollectData> data) {
        SignalCollectListener.super.finished(device, data);
    }

    @Override
    public void finished(CollectInfo info) {
        EventGroupCollectListener.super.finished(info);
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {

    }

    @Override
    public void warnTrigger(WarnConfig config, WarnRecord record, RealtimeStatus status) {
        LinkageActionListener.super.warnTrigger(config, record, status);
    }

    @Override
    public void supplier(Collection<RealtimeStatus> devices) {
        if(CollectionUtil.isNotEmpty(devices)) {
            devices.forEach(device -> {
                try {
                    byte[] bytes = JacksonUtils.getGenericMapper().writeValueAsBytes(device);
                    amqpTemplate.send(IOT_EXCHANGE, IOT_ROUTING_REALTIME_DATA, new Message(bytes));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
