package com.iteaj.iboot.plugin.message;

import com.iteaj.framework.spi.message.MessageManager;
import com.iteaj.framework.spi.message.MessageService;
import com.iteaj.iboot.plugin.message.service.AlibabaService;
import com.iteaj.iboot.plugin.message.service.Sms4jEmailMessageService;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class MessageAutoConfiguration {

    @Bean
    public MessageManager messageManager(List<MessageService> services) {
        return new MessageManager(services);
    }

    @Bean
    public AlibabaService alibabaService() {
        return new AlibabaService();
    }

    @Bean
    public Sms4jEmailMessageService sms4jEmailMessageService() {
        return new Sms4jEmailMessageService();
    }
}
