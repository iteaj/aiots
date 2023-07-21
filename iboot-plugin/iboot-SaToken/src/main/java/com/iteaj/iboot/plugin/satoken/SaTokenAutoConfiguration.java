package com.iteaj.iboot.plugin.satoken;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.AuthenticationService;
import com.iteaj.framework.security.AuthorizationService;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.security.SecurityService;
import com.iteaj.iboot.plugin.satoken.impl.SaTokenSecurityService;
import com.iteaj.iboot.plugin.satoken.impl.StpInterfaceImpl;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

    @Bean
    public SaServletFilter getSaServletFilter(List<OrderFilterChainDefinition> definitions) {
        SaServletFilter servletFilter = new SaServletFilter();
        definitions.forEach(item -> {
            item.getFilterChainMap().forEach((key, value) -> {
                if("anon".endsWith(value)) {
                    servletFilter.addExclude(key);
                } else {
                    servletFilter.addInclude(key);
                }
            });
        });

        return servletFilter
                .setAuth(obj -> StpUtil.checkLogin())
                .setError(e -> {
                    SaHolder.getResponse()
                            .setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                            .setHeader("Content-Type", "application/json;charset=UTF-8");
                    return JSONUtil.toJsonStr(Result.fail("未认证"));
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }
}
