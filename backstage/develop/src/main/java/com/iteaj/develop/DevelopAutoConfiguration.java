package com.iteaj.develop;

import com.iteaj.framework.spring.boot.config.DevelopProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(DevelopProperties.class)
public class DevelopAutoConfiguration {

}
