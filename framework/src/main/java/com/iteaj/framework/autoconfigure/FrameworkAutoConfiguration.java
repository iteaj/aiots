package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.cache.CacheAutoConfiguration;
import com.iteaj.framework.spi.event.EventUtils;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * create time: 2021/3/14
 *  核心框架层的配置信息
 * @author iteaj
 * @since 1.0
 */
@ImportAutoConfiguration({
        ShiroAutoConfiguration.class,
        MybatisPlusConfiguration.class,
        CacheAutoConfiguration.class
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
