package com.iteaj.iboot.plugin.amqp.rabbit.iot.listener;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RabbitListener(queues = {
        RabbitDataListenerPublisher.IOT_QUEUE_REALTIME_STATUS,
        RabbitDataListenerPublisher.IOT_QUEUE_REALTIME_WARN,
        RabbitDataListenerPublisher.IOT_QUEUE_REALTIME_DATA
})
public class RabbitTestHandler {

//    @RabbitHandler
    public void reciveStatus(byte[] message) throws Exception {
//        String s = new String(message.getBody());
//        Object parse = JSONObject.parse(message);
//        System.out.println("rabbit：" + parse);
    }
}
