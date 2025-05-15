package com.iteaj.iboot.plugin.ai;

import com.iteaj.framework.spi.admin.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.iteaj.iboot.plugin.ai")
public class AiAutoConfiguration {

    @Bean
    public Module aiModule() {
        return Module.module("ai", "AI大模型", 1000);
    }
}
