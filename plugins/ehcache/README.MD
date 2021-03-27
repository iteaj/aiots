  ehcahce缓存是用java实现的基于内存的缓存, 直接和系统在同一个jvm使用性能非常高. 一般用于并发量不大, 缓存的数据在系统重启或服务器断电时导致的数据丢失并没有关系,缓存的数据无需考虑线程安全(比如分布式集群)等问题的场景

#### 如何自定义一个缓存
```
// 比如在shiro插件里面需要用到session缓存
@Configuration
@ConditionalOnProperty(value = "izone.web.admin.cache-enable", havingValue = "true")
public class ShiroCacheConfiguration {
   /**
     * session缓存
     * @return
     */
    @Bean
    public CacheEntry sessionCache() {
        FrameworkWebProperties.Admin admin = properties.getAdmin();
        return new CacheEntry(admin.getTokenCacheName() // 只需要创建一个CacheEntry对象指定缓存名,过期时间,序列化类型, 并注入到spring容器
                , admin.getTimeToLiveSeconds() * 1000, SerializerType.Jdk);
    }
}
```
