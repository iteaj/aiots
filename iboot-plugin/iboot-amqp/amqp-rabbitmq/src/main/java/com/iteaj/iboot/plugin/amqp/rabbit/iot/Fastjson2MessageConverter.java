package com.iteaj.iboot.plugin.amqp.rabbit.iot;

import com.alibaba.fastjson2.JSON;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

public class Fastjson2MessageConverter extends AbstractMessageConverter {
    @Override
    protected Message createMessage(Object o, MessageProperties messageProperties) {
        byte[] bytes = JSON.toJSONBytes(o);
        messageProperties.setContentType("application/json");
        messageProperties.setContentEncoding("UTF-8");
        messageProperties.setContentLength(bytes.length);
        return new Message(bytes, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return JSON.parseObject(message.getBody(), Object.class);
    }
}
