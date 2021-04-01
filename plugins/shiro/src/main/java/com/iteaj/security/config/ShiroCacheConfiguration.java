package com.iteaj.security.config;

import com.iteaj.framework.cache.CacheEntry;
import com.iteaj.framework.cache.SerializerType;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import org.apache.shiro.authz.AuthorizationInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create time: 2021/3/22
 *  shiro缓存配置
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class ShiroCacheConfiguration {


    private final FrameworkProperties properties;

    public ShiroCacheConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }

    /**
     * session缓存
     * @return
     */
    @Bean
    public CacheEntry sessionCache() {
        FrameworkProperties.Admin admin = properties.getAdmin();
        return new CacheEntry(admin.getTokenCacheName()
                , admin.getTimeToLiveSeconds() * 1000, SerializerType.Jdk);
    }

}
