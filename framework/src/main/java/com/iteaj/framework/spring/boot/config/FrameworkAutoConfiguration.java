package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.mybatis.MybatisPlusConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * create time: 2021/3/14
 *
 * @author iteaj
 * @since 1.0
 */
@ComponentScan({
        "com.iteaj.*.config", // 注入所有模块的自动配置类
})
@ImportAutoConfiguration(MybatisPlusConfiguration.class)
public class FrameworkAutoConfiguration {

}
