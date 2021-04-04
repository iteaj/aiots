package com.iteaj.security.config;

import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import org.springframework.context.annotation.Configuration;

/**
 * create time: 2021/3/22
 *  shiro缓存配置
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class ShiroCacheConfiguration {


    private final FrameworkWebProperties properties;

    public ShiroCacheConfiguration(FrameworkWebProperties properties) {
        this.properties = properties;
    }


}
