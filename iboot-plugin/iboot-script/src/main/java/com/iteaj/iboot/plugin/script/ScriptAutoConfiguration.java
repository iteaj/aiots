package com.iteaj.iboot.plugin.script;

import com.iteaj.framework.script.ScriptManager;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.iboot.plugin.script.impl.JavaScriptImpl;
import com.iteaj.iboot.plugin.script.model.LinkageScriptRealtimeListener;
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

    /**
     * 场景联动脚本实现监听
     * @return
     */
    @Bean
    public LinkageScriptRealtimeListener linkageScriptRealtimeListener() {
        return new LinkageScriptRealtimeListener();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ScriptManager.register(new JavaScriptImpl());
    }
}
