package com.iteaj.ehcache.config;

import com.iteaj.framework.cache.CacheEntry;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

/**
 * create time: 2021/3/17
 *  ehcache缓存实现
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class EhcacheCacheAutoConfiguration implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    /**
     * 如果redis缓存存在则使用redis缓存
     * @return
     */
    @Bean({"cacheManager", "ehcache3CacheManager"})
    @ConditionalOnMissingBean(name = "org.springframework.data.redis.cache.RedisCacheManager")
    public CacheManager ehcache3CacheManager(ObjectProvider<CacheEntry>  caches) throws URISyntaxException {

        CachingProvider cachingProvider = Caching.getCachingProvider(
                "org.ehcache.jsr107.EhcacheCachingProvider", this.classLoader);

        // 通过配置文件创建缓存
        URI ehcacheResource = this.classLoader.getResource("ehcache3.xml").toURI();
        javax.cache.CacheManager cacheManager = cachingProvider
                .getCacheManager(ehcacheResource, this.classLoader);

        // 初始化缓存
        caches.forEach(item -> {
            cacheManager.createCache(item.getName(), doCreateCache(item));
        });

        return new JCacheCacheManager(cacheManager);
    }

    private javax.cache.configuration.Configuration doCreateCache(CacheEntry item) {

        CacheConfiguration configuration = this.authCacheConfiguration(item);
        return Eh107Configuration.fromEhcacheCacheConfiguration(configuration);
    }

    public CacheConfiguration authCacheConfiguration(CacheEntry item) {

        // ResourcePoolsBuilder 用于构建Cache的各项参数
        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder();

        // ResourcePools 存放Cache各项参数的对象
        // heap() 缓存在内存中的大小,还有其他方法读者自行查看
        ResourcePools resourcePools = resourcePoolsBuilder
                .heap(500, MemoryUnit.MB)
                .disk(1, MemoryUnit.GB, true).build();

        Duration timeToLiveSeconds = Duration.ofMillis(item.getExpire());

        // String.class, Object.class Cache存放key和value的数据类型
        // withExpiry() 设置信息在缓存中的存放的时间，有两种：<br>
        // 1、TTL缓存自创建之时起至失效时的时间间隔；
        // 2、TTI缓存创建以后，最后一次访问缓存之时至失效之时的时间间隔
        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, item.getClazz(), resourcePools)
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(timeToLiveSeconds))
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(timeToLiveSeconds))
                .build();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
