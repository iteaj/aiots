package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.mybatis.MybatisPlusConfiguration;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.auth.AccountAuthHandler;
import com.iteaj.framework.spi.auth.FrameworkAuthServlet;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.spi.event.EventUtils;
import com.iteaj.framework.spi.oauth2.FrameworkOauth2Configuration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

import java.util.List;

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
@ImportAutoConfiguration({
        MybatisPlusConfiguration.class
})
@EnableConfigurationProperties({FrameworkProperties.class})
public class FrameworkAutoConfiguration {

    private final FrameworkProperties properties;

    public FrameworkAutoConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }

    @Bean
    public EventUtils eventUtils() {
        return new EventUtils();
    }

    @Bean
    @ConditionalOnMissingBean({CacheManager.class})
    public CacheManager defaultCacheManager() {
        return new ConcurrentMapCacheManager();
    }

}
