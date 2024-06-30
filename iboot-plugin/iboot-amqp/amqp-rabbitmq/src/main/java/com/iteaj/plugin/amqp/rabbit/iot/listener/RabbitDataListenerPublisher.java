package com.iteaj.plugin.amqp.rabbit.iot.listener;

import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import org.springframework.amqp.core.AmqpTemplate;

public class RabbitDataListenerPublisher implements RealtimeInfoListener {

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

    public RabbitDataListenerPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }


    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        String type = value.getType();
        if(type.equals("data")) {
            value.getPayload().forEach(info -> {
                amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_DATA, info);
            });
        } else if(type.equals("status")) {
            value.getPayload().forEach(info -> {
                amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_STATUS, info);
            });
            if(json instanceof JSONArray) {
                ((JSONArray) json).forEach(info -> {
                    amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_STATUS, info);
                });
            } else {
                amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_STATUS, json);
            }
        } else if(type.equals("warn")) {
            if(json instanceof JSONArray) {
                ((JSONArray) json).forEach(info -> {
                    amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_WARN, info);
                });
            } else {
                amqpTemplate.convertAndSend(IOT_EXCHANGE, IOT_ROUTING_REALTIME_WARN, json);
            }
        }
    }
}
