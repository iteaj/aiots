package com.iteaj.iboot.module.iot.mq;

import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitmqDataListener.IOT_QUEUE_REALTIME_STATUS)
public class RabbitListenerReciver {

    @RabbitHandler
    public void reciveStatus(byte[] message) throws Exception {
//        String s = new String(message.getBody());
        Object parse = JSONObject.parse(message);
        System.out.println("log.all:" + parse);
    }
}
