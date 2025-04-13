package com.iteaj.iboot.plugin.script;

import com.iteaj.framework.script.ScriptManager;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.iboot.plugin.script.impl.JavaScriptImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.iteaj.iboot.plugin.script"})
@MapperScan({"com.iteaj.iboot.plugin.script.mapper"})
public class ScriptAutoConfiguration implements InitializingBean {

    @Bean
    public Module scriptModule() {
        return Module.module("script", "脚本插件", 8);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ScriptManager.register(new JavaScriptImpl());
    }
}
