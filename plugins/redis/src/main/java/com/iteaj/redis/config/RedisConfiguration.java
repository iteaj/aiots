package com.iteaj.redis.config;

import com.iteaj.framework.cache.CacheEntry;
import com.iteaj.framework.cache.SerializerType;
import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import com.iteaj.framework.spring.condition.ConditionalOnFramework;
import com.iteaj.framework.spring.condition.Type;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
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

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean({"cacheManager", "redisCacheManager"})
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, ObjectProvider<CacheEntry> caches) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory);

        // 创建默认的redis缓存配置
        RedisCacheConfiguration defaultCacheConfiguration = this.defaultCacheConfiguration();

        Map<String, RedisCacheConfiguration> initCaches = new HashMap<>();

        // 创建session缓存配置
        caches.forEach(item -> {
            initCaches.put(item.getName(), doCreateCache(item));
        });

        return new RedisCacheManager(redisCacheWriter, defaultCacheConfiguration, initCaches);
    }

    private RedisCacheConfiguration doCreateCache(CacheEntry item) {

        RedisSerializationContext.SerializationPair serializer;
        if(item.getType() == SerializerType.Jdk) {
            // 使用jdk序列化
            serializer = RedisSerializationContext.SerializationPair
                    .fromSerializer(new JdkSerializationRedisSerializer(this.classLoader));
        } else {
            // 使用jackson序列化
            serializer = RedisSerializationContext.SerializationPair
                    .fromSerializer(new Jackson2JsonRedisSerializer<>(item.getClazz()));
        }

        RedisCacheConfiguration sessionCache = RedisCacheConfiguration
                .defaultCacheConfig().serializeValuesWith(serializer);

        // 设置session缓存时间
        Duration timeToLiveSeconds = Duration.ofMillis(item.getExpire());
        sessionCache.entryTtl(timeToLiveSeconds);

        return sessionCache;
    }

    public RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext
                .SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(this.classLoader)));
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
