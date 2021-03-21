package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.captcha.CaptchaConfiguration;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.framework.spi.admin.auth.AuthenticatingService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan({
        "com.iteaj.*.web.**" // 扫描注入所有web模块
})
@ImportAutoConfiguration({
        CaptchaConfiguration.class,
        FrameworkAutoConfiguration.class
})
@EnableConfigurationProperties({FrameworkWebProperties.class})
public class FrameworkWebAutoConfiguration {

    @Bean
    public AuthzUrl authzUrl(FrameworkWebProperties properties) {
        FrameworkWebProperties.Admin auth = properties.getAdmin();

        return new AuthzUrl(auth.getLoginUrl()
                , auth.getSuccessUrl(), auth.getUnauthorizedUrl());
    }

    @Bean
    public SecurityUtils securityUtils(AuthenticatingService authenticatingService) {
        return new SecurityUtils(authenticatingService);
    }

    /**
     * web 框架配置
     */
    @Configuration
    @ConditionalOnWebApplication
    public static class WebMvcConfiguration implements WebMvcConfigurer {

    }
}

