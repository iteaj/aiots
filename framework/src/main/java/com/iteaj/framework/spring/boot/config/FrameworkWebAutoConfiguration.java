package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.security.AuthzUrl;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan({
        "com.iteaj.modules.*.controller" // 扫描所有web模块的controller层
})
@ImportAutoConfiguration(FrameworkAutoConfiguration.class)
@EnableConfigurationProperties({FrameworkWebProperties.class})
public class FrameworkWebAutoConfiguration {

    @Bean
    public AuthzUrl authzUrl(FrameworkWebProperties properties) {
        FrameworkWebProperties.Auth auth = properties.getAuth();

        return new AuthzUrl(auth.getLoginUrl()
                , auth.getSuccessUrl(), auth.getUnauthorizedUrl());
    }

    /**
     * web 框架配置
     */
    @Configuration
    @ConditionalOnWebApplication
    public static class WebMvcConfiguration implements WebMvcConfigurer {

    }
}

