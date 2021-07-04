package com.iteaj.oauth2.config;

import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.oauth2.client.JustOauth2AuthHandler;
import com.iteaj.oauth2.client.OAuth2HandleServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * create time: 2021/3/21
 * OAuth2客户端配置
 * @author iteaj
 * @since 1.0
 */
@ConditionalOnWebApplication
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class OAuth2ClientConfiguration {

    private final OAuth2ClientProperties clientProperties;

    public OAuth2ClientConfiguration(OAuth2ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    /**
     * Oauth2认证授权处理器
     * @param actions
     * @return
     */
    @Bean
    @Primary
    public WebAuthHandler oauth2AuthHandle(List<WebAuthAction> actions, CacheManager cacheManager) {
        return new JustOauth2AuthHandler(actions, cacheManager);
    }

    /**
     * 框架的授权,认证登录, 注销 Servlet
     * @param handler
     * @return
     */
    @Bean
    public ServletRegistrationBean frameworkAuthServlet(WebAuthHandler handler) {
        String redirectUri = clientProperties.getRedirectUri();
        return new ServletRegistrationBean(new OAuth2HandleServlet(handler), redirectUri);
    }

    /**
     * oauth2重定向地址不能被过滤器拦截
     * @return
     */
    @Bean
    @Order(20)
    public OrderFilterChainDefinition oauth2FilterChainDefinition() {
        final OrderFilterChainDefinition definition = new OrderFilterChainDefinition();

        definition.addPathDefinition(clientProperties.getRedirectUri(), "anon");
        return definition;
    }
}
