package com.iteaj.security.config;

import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.framework.spring.condition.ConditionalOnFramework;
import com.iteaj.framework.spring.condition.Type;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

/**
 * create time: 2021/3/23
 *  基于shiro实现的app认证 + session管理
 * @author iteaj
 * @since 1.0
 */
@Configuration
@ConditionalOnFramework(Type.App)
@AutoConfigureBefore(ShiroAutoConfiguration.class)
public class ShiroAppConfiguration {

    private final FrameworkProperties properties;

    public ShiroAppConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }

}
