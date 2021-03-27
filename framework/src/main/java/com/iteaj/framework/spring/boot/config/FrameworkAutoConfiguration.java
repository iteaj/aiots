package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.mybatis.MybatisPlusConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * create time: 2021/3/14
 *  核心框架层的配置信息
 * @author iteaj
 * @since 1.0
 */
@ComponentScan({
        "com.iteaj.*.config", // 注入所有模块的自动配置类
        "com.iteaj.*.service.**" // 注入业务层组件
})
@MapperScan(basePackages = {"com.iteaj.*.mapper"}) // 注入dao层组件
@ImportAutoConfiguration(MybatisPlusConfiguration.class)
public class FrameworkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({CacheManager.class})
    public CacheManager defaultCacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
