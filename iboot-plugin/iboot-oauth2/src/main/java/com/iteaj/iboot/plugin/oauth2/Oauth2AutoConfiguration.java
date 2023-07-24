package com.iteaj.iboot.plugin.oauth2;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.iboot.plugin.oauth2.sa.SaOAuth2TemplateImpl;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;

@ComponentScan("com.iteaj.iboot.plugin.oauth2.controller")
public class Oauth2AutoConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        event.getEnvironment().addActiveProfile("oauth2");
    }

    @Bean
    public OrderFilterChainDefinition oauth2FilterChainDefinition() {
        return new OrderFilterChainDefinition().addAnon("/oauth2/**");
    }

    @Bean
    public SaOAuth2Template saOAuth2TemplateImpl() {
        return new SaOAuth2TemplateImpl();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }
}
