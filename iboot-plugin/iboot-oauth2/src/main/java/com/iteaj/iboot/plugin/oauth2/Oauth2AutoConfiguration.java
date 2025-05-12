package com.iteaj.iboot.plugin.oauth2;

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

@MapperScan("com.iteaj.iboot.plugin.oauth2.mapper")
@ComponentScan("com.iteaj.iboot.plugin.oauth2")
public class Oauth2AutoConfiguration implements Plugin {


    @Override
    public String getPluginName() {
        return "Oauth2";
    }

    @Override
    public String getPluginDesc() {
        return "基于SaToken实现的Oauth2 Server授权框架";
    }
}
