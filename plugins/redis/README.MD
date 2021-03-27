## redis是一款高性能的基于键值对且提供多种数个类型的缓存服务, 在很多系统架构中都会用到redis. 官方给出的数字是读写性能可以达到10万/秒,且提供持久化和主从复制等集群功能实现了高可用, 所以我们也提供了对它的支持
### 如何自定义一个缓存
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
