package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.cache.FrameworkCacheConfiguration;
import com.iteaj.framework.mybatis.MybatisPlusConfiguration;
import com.iteaj.framework.spi.event.EventUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
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
@ImportAutoConfiguration({
        MybatisPlusConfiguration.class,
        FrameworkCacheConfiguration.class
})
@EnableConfigurationProperties({FrameworkWebProperties.class})
public class FrameworkAutoConfiguration {

    private final FrameworkWebProperties properties;

    public FrameworkAutoConfiguration(FrameworkWebProperties properties) {
        this.properties = properties;
    }

    @Bean
    public EventUtils eventUtils() {
        return new EventUtils();
    }

}
