package com.iteaj.framework.spring.boot.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * create time: 2021/3/21
 *  app端的配置信息
 * @author iteaj
 * @since 1.0
 */
@ComponentScan({
        "com.iteaj.*.app.**", // 注入所有app相关组件
})
@ImportAutoConfiguration({
        FrameworkAutoConfiguration.class
})
public class FrameworkAppConfiguration {


    public static class AppProperties {

    }
}
