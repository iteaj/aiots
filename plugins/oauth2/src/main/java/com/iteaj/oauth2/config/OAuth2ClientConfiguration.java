package com.iteaj.oauth2.config;

import com.iteaj.oauth2.client.OAuth2Interceptors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * create time: 2021/3/21
 * OAuth2客户端配置
 * @author iteaj
 * @since 1.0
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(OAuth2Properties.class)
public class OAuth2ClientConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OAuth2Interceptors());
    }
}
