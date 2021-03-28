package com.iteaj.oauth2.config;

import com.iteaj.framework.cache.CacheEntry;
import com.iteaj.framework.cache.SerializerType;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.oauth2.client.JustOauth2AuthHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * create time: 2021/3/21
 * OAuth2客户端配置
 * @author iteaj
 * @since 1.0
 */
@Configuration
@ConditionalOnWebApplication
public class OAuth2ClientConfiguration implements WebMvcConfigurer {

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
     * 授权上下文缓存
     * key: state value: AuthContext
     * @see JustOauth2AuthHandler#cacheStateAndContextMap(String, AuthContext)
     * @see AuthContext
     * @return
     */
    @Bean
    public CacheEntry oauth2StateCache() {
        return new CacheEntry(CoreConst.OAUTH2_STATE_CACHE, 3 * 60 * 1000l, SerializerType.Jdk);
    }
}
