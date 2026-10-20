package com.iteaj.iboot.plugin.oauth2;

import com.iteaj.framework.ProfilesInclude;
import com.iteaj.framework.plugin.Plugin;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.iboot.plugin.oauth2.config.Oauth2ClientProperties;
import com.iteaj.iboot.plugin.oauth2.sa.OpenApiFilter;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2AppService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

@ProfilesInclude("oauth2")
@MapperScan("com.iteaj.iboot.plugin.oauth2.mapper")
@ComponentScan("com.iteaj.iboot.plugin.oauth2")
@EnableConfigurationProperties(Oauth2ClientProperties.class)
public class Oauth2AutoConfiguration implements Plugin {

    @Bean
    @Order(value = 18)
    public OrderFilterChainDefinition oauth2FilterChainDefinition() {
        return new OrderFilterChainDefinition()
                .addAnon("/oauth2/client/authorize", "/oauth2/client/callback", "/oauth2/client/login");
    }

    @Bean
    public FilterRegistrationBean<OpenApiFilter> openApiFilter(Oauth2AppService oauth2AppService) {
        FilterRegistrationBean<OpenApiFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OpenApiFilter(oauth2AppService));
        registrationBean.setOrder(105); // 此值必须大于-105且小于SaServletFilter的order值
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Override
    public String getPluginName() {
        return "Oauth2";
    }

    @Override
    public String getPluginDesc() {
        return "基于SaToken实现的Oauth2 Server授权框架";
    }
}
