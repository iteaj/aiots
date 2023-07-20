package com.iteaj.iboot.plugin.swagger;

import com.iteaj.framework.security.OrderFilterChainDefinition;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

public class Knife4jAutoConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    @Bean
    public OrderFilterChainDefinition swaggerFilterChainDefinition() {
        return new OrderFilterChainDefinition().addAnon("/swagger/**", "/doc.html", "/webjars/**", "/openapi/**", "/v3/api-docs/**");
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        event.getEnvironment().addActiveProfile("knife4j");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }
}
