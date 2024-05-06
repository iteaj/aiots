package com.iteaj.plugin.amqp.rabbit;

import com.iteaj.framework.json.JacksonUtils;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
}
