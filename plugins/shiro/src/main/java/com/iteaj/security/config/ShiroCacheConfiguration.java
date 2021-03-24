package com.iteaj.security.config;

import com.iteaj.framework.cache.CacheEntry;
import com.iteaj.framework.cache.SerializerType;
import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import org.apache.shiro.authz.AuthorizationInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create time: 2021/3/22
 *  shiro缓存配置
 * @author iteaj
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(value = "izone.web.admin.cache-enable", havingValue = "true")
public class ShiroCacheConfiguration {


    private final FrameworkWebProperties properties;

    public ShiroCacheConfiguration(FrameworkWebProperties properties) {
        this.properties = properties;
    }

    /**
     * session缓存
     * @return
     */
    @Bean
    public CacheEntry sessionCache() {
        FrameworkWebProperties.Admin admin = properties.getAdmin();
        return new CacheEntry(admin.getTokenCacheName()
                , admin.getTimeToLiveSeconds() * 1000, SerializerType.Jdk);
    }

    /**
     * 认证信息缓存
     * @see org.apache.shiro.authc.AuthenticationInfo
     * @see org.apache.shiro.subject.PrincipalCollection
     * @return
     */
    @Bean
    public CacheEntry authCache() {
        FrameworkWebProperties.Admin admin = properties.getAdmin();
        return new CacheEntry(admin.getAuthCacheName()
                , admin.getTimeToLiveSeconds() * 1000, SerializerType.Jdk);
    }

    /**
     * 权限信息缓存
     * @see AuthorizationInfo
     * @return
     */
    @Bean
    public CacheEntry authorCache() {
        FrameworkWebProperties.Admin admin = properties.getAdmin();
        return new CacheEntry(admin.getAuthorCacheName()
                , admin.getTimeToLiveSeconds() * 1000, SerializerType.Jdk);
    }
}
