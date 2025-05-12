package com.iteaj.iboot.plugin.amqp.rabbit;

import com.iteaj.framework.ProfilesInclude;
import com.iteaj.iboot.plugin.amqp.rabbit.iot.Fastjson2MessageConverter;
import com.iteaj.iboot.plugin.amqp.rabbit.iot.listener.RabbitDataListenerPublisher;
import com.iteaj.iboot.plugin.amqp.rabbit.iot.listener.RabbitTestHandler;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;

@ProfilesInclude("rabbit")
public class RabbitPluginAutoConfiguration {

    /**
     * 使用json做消息转换
     * @param properties
     * @return
     */
    @Bean
    public RabbitTemplateConfigurer rabbitTemplateConfigurer(RabbitProperties properties) {
        RabbitTemplateConfigurer rabbitTemplateConfigurer = new RabbitTemplateConfigurer(properties);
        rabbitTemplateConfigurer.setMessageConverter(new Fastjson2MessageConverter());
        return rabbitTemplateConfigurer;
    }

    @Bean
    public RabbitDataListenerPublisher rabbitDataListenerPublisher(AmqpTemplate amqpTemplate) {
        return new RabbitDataListenerPublisher(amqpTemplate);
    }

    @Bean
    public RabbitTestHandler rabbitTestHandler() {
        return new RabbitTestHandler();
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
    public Queue warnQueue() {
        return new Queue(RabbitDataListenerPublisher.IOT_QUEUE_REALTIME_WARN);
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
    public Binding warnBinding(Queue warnQueue, DirectExchange iotExchange) {
        return BindingBuilder.bind(warnQueue).to(iotExchange).with(RabbitDataListenerPublisher.IOT_ROUTING_REALTIME_WARN);
    }

    @Bean
    public Binding dataBinding(Queue dataQueue, DirectExchange iotExchange) {
        return BindingBuilder.bind(dataQueue).to(iotExchange).with(RabbitDataListenerPublisher.IOT_ROUTING_REALTIME_DATA);
    }
}
