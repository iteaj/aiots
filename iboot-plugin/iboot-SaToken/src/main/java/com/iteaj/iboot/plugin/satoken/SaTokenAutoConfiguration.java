package com.iteaj.iboot.plugin.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.iteaj.framework.security.AuthenticationService;
import com.iteaj.framework.security.AuthorizationService;
import com.iteaj.framework.security.SecurityService;
import com.iteaj.iboot.plugin.satoken.impl.SaTokenSecurityService;
import com.iteaj.iboot.plugin.satoken.impl.StpInterfaceImpl;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

public class SaTokenAutoConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        event.getEnvironment().addActiveProfile("satoken");
    }

    @Bean
    public StpInterface stpInterfaceImpl(AuthorizationService authorizationService) {
        return new StpInterfaceImpl(authorizationService);
    }

    @Bean
    public SecurityService securityService(AuthenticationService authenticationService) {
        return new SaTokenSecurityService(authenticationService);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }
}
