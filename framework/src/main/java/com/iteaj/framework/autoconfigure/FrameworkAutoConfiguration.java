package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.cache.FrameworkCacheConfiguration;
import com.iteaj.framework.mybatis.MybatisPlusConfiguration;
import com.iteaj.framework.spi.event.EventUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * create time: 2021/3/14
 *  核心框架层的配置信息
 * @author iteaj
 * @since 1.0
 */
@ComponentScan({
        "com.iteaj.msn.*", // 注入各个模块的自动配置类
        "com.iteaj.*.config" // 扫描启用各个插件的配置类
})
@MapperScan(basePackages = {"com.iteaj.msn.*.mapper"}) // 注入各个模块dao层组件
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
