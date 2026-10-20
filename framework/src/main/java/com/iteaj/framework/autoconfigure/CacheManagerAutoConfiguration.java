package com.iteaj.framework.autoconfigure;

import com.alibaba.fastjson2.support.spring6.data.redis.GenericFastJsonRedisSerializer;
import com.iteaj.framework.cache.CacheEntry;
import com.iteaj.framework.cache.SerializerType;
import com.iteaj.framework.spring.condition.ConditionalOnCluster;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * create time: 2021/4/3
 *
 * @author iteaj
 * @since 1.0
 */
@EnableCaching
@ImportAutoConfiguration(CacheManagerAutoConfiguration.RedisConfiguration.class)
public class CacheManagerAutoConfiguration {

    /**
     * 默认的缓存
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({CacheManager.class})
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    /**
     * Redis缓存配置
     */
    @ConditionalOnCluster
    @ConditionalOnClass(name = {"org.springframework.data.redis.connection.RedisConnectionFactory"})
    public static class RedisConfiguration {

        @Primary
        @Bean({"cacheManager", "redisCacheManager"})
        public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, ObjectProvider<CacheEntry> caches) {
            RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory);

            final ClassLoader classLoader = getClass().getClassLoader();

            // 创建默认的redis缓存配置
            RedisCacheConfiguration defaultCacheConfiguration = this.defaultCacheConfiguration(classLoader);

            Map<String, RedisCacheConfiguration> initCaches = new HashMap<>();

            // 创建session缓存配置
            caches.forEach(item -> {
                initCaches.put(item.getName(), doCreateCache(item, classLoader));
            });

            return new RedisCacheManager(redisCacheWriter, defaultCacheConfiguration, initCaches);
        }

        private RedisCacheConfiguration doCreateCache(CacheEntry item, ClassLoader classLoader) {
            RedisSerializationContext.SerializationPair serializer;
            if(item.getType() == SerializerType.Jdk) {
                // 使用jdk序列化
                serializer = RedisSerializationContext.SerializationPair
                        .fromSerializer(new JdkSerializationRedisSerializer(classLoader));
            } else {
                // 使用Fastjson序列化
                serializer = RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericFastJsonRedisSerializer());
            }

            RedisCacheConfiguration sessionCache = RedisCacheConfiguration
                    .defaultCacheConfig().serializeValuesWith(serializer);

            // 设置session缓存时间
            Duration timeToLiveSeconds = Duration.ofSeconds(item.getExpire());
            sessionCache.entryTtl(timeToLiveSeconds);

            return sessionCache;
        }

        public RedisCacheConfiguration defaultCacheConfiguration(ClassLoader classLoader) {
            return RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext
                    .SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));
        }
    }

}
