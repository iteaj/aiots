package com.iteaj.redis.config;

import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * create time: 2021/3/20
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class RedisConfiguration implements BeanClassLoaderAware {

    private ClassLoader classLoader;
    private final FrameworkWebProperties properties;

    public RedisConfiguration(FrameworkWebProperties properties) {
        this.properties = properties;
    }

    @Bean({"cacheManager", "redisCacheManager"})
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory);

        // 创建默认的redis缓存配置
        RedisCacheConfiguration defaultCacheConfiguration = this.defaultCacheConfiguration();

        // 创建session缓存配置
        Map<String, RedisCacheConfiguration> map = this.createSessionCache();

        return new RedisCacheManager(redisCacheWriter, defaultCacheConfiguration, map);
    }

    public RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext
                .SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(this.classLoader)));
    }

    /**
     * 只缓存session到redis
     */
    private Map<String, RedisCacheConfiguration> createSessionCache() {
        // 后台管理端的配置信息
        FrameworkWebProperties.Admin admin = properties.getAdmin();

        // 使用jdk序列化指
        RedisSerializationContext.SerializationPair serializer = RedisSerializationContext
                .SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(this.classLoader));

        Map<String, RedisCacheConfiguration> caches = new HashMap<>();
        if(admin != null && admin.isCacheEnable()) {
            RedisCacheConfiguration sessionCache = RedisCacheConfiguration
                    .defaultCacheConfig().serializeValuesWith(serializer);

            caches.put(admin.getTokenCacheName(), sessionCache);
        }

        return caches;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
