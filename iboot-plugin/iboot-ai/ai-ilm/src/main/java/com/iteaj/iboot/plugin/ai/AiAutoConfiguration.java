package com.iteaj.iboot.plugin.ai;

import com.iteaj.framework.spi.admin.Module;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * AI插件自动配置类
 */
@ComponentScan({"com.iteaj.iboot.plugin.ai"})
@MapperScan({"com.iteaj.iboot.plugin.ai.mapper"})
@EnableConfigurationProperties(AiProperties.class)
@ConditionalOnProperty(prefix = "iboot.ai", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AiAutoConfiguration {

    private final AiProperties aiProperties;

    public AiAutoConfiguration(AiProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    @Bean
    public Module aiModule() {
        return Module.module("ai", "AI模块", 100);
    }

}
