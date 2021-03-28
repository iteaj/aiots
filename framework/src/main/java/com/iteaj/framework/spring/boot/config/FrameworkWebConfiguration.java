package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.captcha.CaptchaConfiguration;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.framework.spi.admin.auth.AuthenticatingService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 后台管理端的配置信息
 */
@ComponentScan({
        "com.iteaj.*.web.**" // 扫描注入所有web模块
})
@ImportAutoConfiguration({
        CaptchaConfiguration.class,
        FrameworkAutoConfiguration.class
})
public class FrameworkWebConfiguration {

    @Bean
    public AuthzUrl authzUrl(FrameworkProperties properties) {
        FrameworkProperties.Admin auth = properties.getAdmin();

        return new AuthzUrl(auth.getLoginUrl()
                , auth.getSuccessUrl(), auth.getUnauthorizedUrl());
    }

    @Bean
    public SecurityUtils securityUtils(AuthenticatingService authenticatingService) {
        return new SecurityUtils(authenticatingService);
    }

    @Getter
    @Setter
    public static class AdminProperties {
        /**
         * 登录页的url(GET)和提交的url(POST)必须相同
         */
        private String loginUrl;

        /** 认证成功后要跳转的Url*/
        private String successUrl;

        /** 认证失败后要跳转的Url */
        private String unauthorizedUrl;

        /**
         * 缓存的存活时间 (默认时间30分钟)
         */
        private long timeToLiveSeconds = 30 * 60;

        /**
         * 设置对象在失效前的允许闲置时间（单位：秒）
         * 默认0 说明此属性无效(也就是可闲置时间无穷大)
         */
        private long timeToIdleSeconds = 10 * 60;

        /**
         * 是否允许缓存token(redis只缓存session)
         */
        private boolean cacheEnable;

        /**
         * 令牌(token)的缓存名称
         */
        private String tokenCacheName = "izone_token_cache";

        /**
         * 认证信息的缓存名称
         */
        private String authCacheName = "izone_authentication_cache";

        /**
         * 权限信息的缓存名称
         */
        private String authorCacheName = "izone_authorization_cache";


        /** 验证码类型, 默认数字相加 e.g. 3+3*/
        private String captchaType = "math";

        /** 是否开启验证码 */
        private boolean captchaEnabled = true;

    }
}

