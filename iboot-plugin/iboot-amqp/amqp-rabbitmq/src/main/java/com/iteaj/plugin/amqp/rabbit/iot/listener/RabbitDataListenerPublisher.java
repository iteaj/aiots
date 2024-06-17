package com.iteaj.plugin.amqp.rabbit.iot.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

public class RabbitDataListenerPublisher implements RealtimeInfoListener {

    private final AmqpTemplate amqpTemplate;
    // exchange
    public final static String IOT_EXCHANGE = "iot-exchange";

    // 实时数据队列和key
    public final static String IOT_QUEUE_REALTIME_DATA = "iot.queue.realtime.data";
    public final static String IOT_ROUTING_REALTIME_DATA = "iot.routing.realtime.data";

    // 实时设备状态队列和key
    public final static String IOT_QUEUE_REALTIME_STATUS = "iot.queue.realtime.status";
    public final static String IOT_ROUTING_REALTIME_STATUS = "iot.routing.realtime.status";

    public RabbitDataListenerPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        String type = value.getType();
        JSON json = value.getJson();
        if(type.equals("data")) {
            if(json instanceof JSONArray) {
                ((JSONArray) json).forEach(info -> {
                    amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_DATA, info);
                });
            } else {
                amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_DATA, json);
            }
        } else if(type.equals("status")) {
            if(json instanceof JSONArray) {
                ((JSONArray) json).forEach(info -> {
                    amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_STATUS, info);
                });
            } else {
                amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_STATUS, json);
            }
        }
    }
}
