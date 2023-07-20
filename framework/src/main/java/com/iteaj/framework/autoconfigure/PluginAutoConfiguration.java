package com.iteaj.framework.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * 用来配置iboot-plugin下的模块
 */
@ComponentScan({"com.iteaj.iboot.plugin"})
@PropertySource({"classpath:/plugin/application-swagger.properties"})
public class PluginAutoConfiguration {

    public PluginAutoConfiguration() {
        System.out.println();
    }
}
