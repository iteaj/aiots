package com.iteaj.plugin;

import com.iteaj.redis.shiro.ShiroRedisSessionCacheManager;
import org.apache.shiro.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create time: 2021/7/3
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class ShiroRedisAutoConfiguration {

    @Bean
    public CacheManager shiroRedisCacheManager() {
        return new ShiroRedisSessionCacheManager();
    }
}
