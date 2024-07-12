package com.iteaj.plugin.amqp.rabbit;

import com.iteaj.framework.json.JacksonUtils;
import com.iteaj.plugin.amqp.rabbit.iot.listener.RabbitDataListenerPublisher;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;

public class RabbitPluginAutoConfiguration {

    /**
     * 使用json做消息转换
     * @param properties
     * @return
     */
    @Bean
    public RabbitTemplateConfigurer rabbitTemplateConfigurer(RabbitProperties properties) {
        RabbitTemplateConfigurer rabbitTemplateConfigurer = new RabbitTemplateConfigurer(properties);
        rabbitTemplateConfigurer.setMessageConverter(new Jackson2JsonMessageConverter(JacksonUtils.getMapper()));
        return rabbitTemplateConfigurer;
    }

    @Bean
    public RabbitDataListenerPublisher rabbitDataListenerPublisher(AmqpTemplate amqpTemplate) {
        return new RabbitDataListenerPublisher(amqpTemplate);
    }

    @Bean
    public Queue statusQueue() {
        return new Queue(RabbitDataListenerPublisher.IOT_QUEUE_REALTIME_STATUS);
    }

    @Bean
    public Queue dataQueue() {
        return new Queue(RabbitDataListenerPublisher.IOT_QUEUE_REALTIME_DATA);
    }

    @Bean
    public DirectExchange iotExchange() {
        return new DirectExchange(RabbitDataListenerPublisher.IOT_EXCHANGE);
    }

    @Bean
    public Binding statusBinding(Queue statusQueue, DirectExchange iotExchange) {
        return BindingBuilder.bind(statusQueue).to(iotExchange).with(RabbitDataListenerPublisher.IOT_ROUTING_REALTIME_STATUS);
    }

    @Bean
    public Binding dataBinding(Queue dataQueue, DirectExchange iotExchange) {
        return BindingBuilder.bind(dataQueue).to(iotExchange).with(RabbitDataListenerPublisher.IOT_ROUTING_REALTIME_DATA);
    }
}
