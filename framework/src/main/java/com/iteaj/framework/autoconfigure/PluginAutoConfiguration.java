package com.iteaj.framework.autoconfigure;

import com.iteaj.framework.plugin.Plugin;
import com.iteaj.framework.plugin.PluginManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

/**
 * 此种配置方式主要用于扫描插件的自动配置类
 * 1. 适合不想要创建META-INF/xx.imports文件的场景
 * 2. 可能会出现bean的加载顺序问题, 如果出现这个问题应考虑用META-INF/xx.imports文件
 */
@ComponentScan({
        "com.iteaj.iboot.plugin.*.autoconfig"
})
public class PluginAutoConfiguration {

    @Bean
    public PluginManagerService pluginManagerService(@Autowired(required = false) List<Plugin> plugins) {
        return new PluginManagerService(plugins);
    }
}
