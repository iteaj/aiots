package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.spi.event.EventUtils;
import com.iteaj.framework.spring.SpringUtils;
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
        PluginAutoConfiguration.class,
        MybatisPlusConfiguration.class,
        RedisJsonAutoConfiguration.class,
        CacheManagerAutoConfiguration.class,
})
@EnableConfigurationProperties({FrameworkProperties.class})
public class FrameworkAutoConfiguration {

    @Bean
    public EventUtils eventUtils() {
        return new EventUtils();
    }

    @Bean
    public SpringUtils springUtils() {
        return SpringUtils.getInstance();
    }

}
