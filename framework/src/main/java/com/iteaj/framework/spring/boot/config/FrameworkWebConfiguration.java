package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.captcha.CaptchaConfiguration;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.framework.spi.admin.auth.FrameworkAuthService;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.auth.AccountAuthHandler;
import com.iteaj.framework.spi.auth.FrameworkAuthServlet;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.spi.oauth2.FrameworkOauth2Configuration;
import lombok.Data;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * web项目配置信息
 */
@ComponentScan({
        "com.iteaj.*.web.**" // 扫描注入所有web模块
})
@ImportAutoConfiguration({
        CaptchaConfiguration.class,
        FrameworkAutoConfiguration.class,
        FrameworkOauth2Configuration.class
})
public class FrameworkWebConfiguration {

    private final FrameworkProperties properties;

    public FrameworkWebConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SecurityUtils securityUtils(FrameworkAuthService frameworkAuthService) {
        return new SecurityUtils(frameworkAuthService);
    }
    @Bean
    @Order(20)
    public OrderFilterChainDefinition oauth2FilterChainDefinition() {
        final OrderFilterChainDefinition definition = new OrderFilterChainDefinition();

        definition.addPathDefinition(properties.getAuth().getLogoutUri(), "anon");
        definition.addPathDefinition(properties.getOauth2().getRedirectUri(), "anon");
        return definition;
    }

    /**
     * 框架的授权,认证登录, 注销服务
     * @param handler
     * @return
     */
    @Bean
    public ServletRegistrationBean frameworkAuthServlet(WebAuthHandler handler) {
        String redirectUri = properties.getOauth2().getRedirectUri();
        final String logoutUri = properties.getAuth().getLogoutUri();
        return new ServletRegistrationBean(new FrameworkAuthServlet(handler), redirectUri, logoutUri);
    }

    /**
     * 使用账号密码认证的处理器
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({WebAuthHandler.class})
    public WebAuthHandler appAuthHandler(List<WebAuthAction> actions) {
        return new AccountAuthHandler(actions);
    }

    @Data
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

        /** 验证码类型, 默认数字相加 e.g. 3+3*/
        private String captchaType = "math";

        /** 是否开启验证码 */
        private boolean captchaEnabled = true;

    }
}

