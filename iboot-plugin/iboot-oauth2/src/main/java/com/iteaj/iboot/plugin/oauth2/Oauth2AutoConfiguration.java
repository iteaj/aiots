package com.iteaj.iboot.plugin.oauth2;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import com.iteaj.framework.plugin.Plugin;
import com.iteaj.iboot.plugin.oauth2.sa.OpenApiFilter;
import com.iteaj.iboot.plugin.oauth2.sa.SaOAuth2TemplateImpl;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2AppService;
import com.iteaj.iboot.plugin.oauth2.service.Oauth2UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;

import javax.servlet.Filter;

@MapperScan("com.iteaj.iboot.plugin.oauth2.mapper")
@ComponentScan("com.iteaj.iboot.plugin.oauth2")
public class Oauth2AutoConfiguration implements Plugin {

    @Bean
    public SaOAuth2Template saOAuth2TemplateImpl(Oauth2AppService oauth2AppService, Oauth2UserService oauth2UserService) {
        return new SaOAuth2TemplateImpl(oauth2AppService, oauth2UserService);
    }

    @Bean
    public FilterRegistrationBean openApiFilter(Oauth2AppService oauth2AppService) {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
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
