package com.iteaj.ehcache.config;

import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.ResourceUnit;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
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

    @Autowired
    private FrameworkWebProperties properties;

    /**
     * 如果redis缓存存在则使用redis缓存
     * @return
     */
    @Bean({"cacheManager", "ehcache3CacheManager"})
    @ConditionalOnMissingBean(name = "org.springframework.data.redis.cache.RedisCacheManager")
    public JCacheCacheManager ehcache3CacheManager() throws URISyntaxException {

        CachingProvider cachingProvider = Caching.getCachingProvider(
                "org.ehcache.jsr107.EhcacheCachingProvider", this.classLoader);

        // 通过配置文件创建缓存
        URI ehcacheResource = this.classLoader.getResource("ehcache3.xml").toURI();
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager(ehcacheResource, this.classLoader);

        // 创建权限认证信息和令牌信息缓存
        this.createAuthAndTokenCache(cacheManager);

        return new JCacheCacheManager(cacheManager);
    }

    private void createAuthAndTokenCache(CacheManager cacheManager) {
        // 后台管理端的配置信息
        FrameworkWebProperties.Admin admin = properties.getAdmin();

        if(admin != null && admin.isCacheEnable()) {

            CacheConfiguration cacheConfiguration = createAuthCacheConfiguration();

            this.createAuthCache(cacheManager, cacheConfiguration);
            this.createTokenCache(cacheManager, cacheConfiguration);
            this.createAuthorCache(cacheManager, cacheConfiguration);
        }
    }

    public void createTokenCache(CacheManager cacheManager, CacheConfiguration authCacheConfiguration) {
        // 后台管理端的配置信息
        FrameworkWebProperties.Admin admin = properties.getAdmin();

        javax.cache.configuration.Configuration jcacheConfiguration =
                Eh107Configuration.fromEhcacheCacheConfiguration(authCacheConfiguration);

        cacheManager.createCache(admin.getTokenCacheName(), jcacheConfiguration);
    }

    /**
     * 权限信息缓存
     * @param cacheManager
     * @param authCacheConfiguration
     * @return
     */
    public void createAuthorCache(CacheManager cacheManager, CacheConfiguration authCacheConfiguration) {
        // 后台管理端的配置信息
        FrameworkWebProperties.Admin admin = properties.getAdmin();

        javax.cache.configuration.Configuration jcacheConfiguration =
                Eh107Configuration.fromEhcacheCacheConfiguration(authCacheConfiguration);

        cacheManager.createCache(admin.getAuthorCacheName(), jcacheConfiguration);
    }

    /**
     * 认证信息缓存
     * @param cacheManager
     * @param authCacheConfiguration
     * @return
     */
    public void createAuthCache(CacheManager cacheManager, CacheConfiguration authCacheConfiguration) {
        // 后台管理端的配置信息
        FrameworkWebProperties.Admin admin = properties.getAdmin();

        javax.cache.configuration.Configuration jcacheConfiguration =
                Eh107Configuration.fromEhcacheCacheConfiguration(authCacheConfiguration);

        cacheManager.createCache(admin.getAuthCacheName(), jcacheConfiguration);
    }

    public CacheConfiguration createAuthCacheConfiguration() {
        // 后台管理端的配置信息
        FrameworkWebProperties.Admin admin = properties.getAdmin();

        // ResourcePoolsBuilder 用于构建Cache的各项参数
        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder();

        // ResourcePools 存放Cache各项参数的对象
        // heap() 缓存在内存中的大小,还有其他方法读者自行查看
        ResourcePools resourcePools = resourcePoolsBuilder
                .heap(500, MemoryUnit.MB)
                .disk(1, MemoryUnit.GB, true).build();

        // String.class, Object.class Cache存放key和value的数据类型
        // withExpiry() 设置信息在缓存中的存放的时间，有两种：<br>
        // 1、TTL缓存自创建之时起至失效时的时间间隔；
        // 2、TTI缓存创建以后，最后一次访问缓存之时至失效之时的时间间隔
        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, Object.class, resourcePools)
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(admin.getTimeToLiveSeconds())))
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(admin.getTimeToIdleSeconds())))
                .build();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
