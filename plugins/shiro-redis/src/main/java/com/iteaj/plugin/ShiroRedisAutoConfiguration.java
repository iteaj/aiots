package com.iteaj.plugin;

import com.iteaj.redis.shiro.ShiroRedisSessionCacheManager;
import org.apache.shiro.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * create time: 2021/7/3
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class ShiroRedisAutoConfiguration {

    /**
     * 只能使用Jdk序列化
     * @param redisConnectionFactory
     * @return
     */
    @Bean("shiroRedisTemplate")
    public RedisTemplate shiroRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(redisTemplate.getStringSerializer());
        redisTemplate.setHashKeySerializer(redisTemplate.getStringSerializer());

        return redisTemplate;
    }

    @Bean
    public CacheManager shiroRedisCacheManager() {
        return new ShiroRedisSessionCacheManager();
    }
}
