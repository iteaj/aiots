package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.security.AuthzUrl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({FrameworkProperties.class})
public class FrameworkConfiguration {

    @Bean
    public AuthzUrl authzUrl(FrameworkProperties properties) {
        FrameworkProperties.Auth auth = properties.getAuth();

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

