package com.iteaj.iboot.module.app;

import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.Module;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

/**
 * 移动端App模块自动配置
 * @author iteaj
 */
@ComponentScan({"com.iteaj.iboot.module.app"})
@MapperScan("com.iteaj.iboot.module.app.mapper")
public class AppAutoConfiguration {

    public Module appModule() {
        return Module.module("app", "移动端模块", 100, "m");
    }

    /**
     * 移动端公开接口无需认证
     */
    @Bean
    @Order(value = 18)
    public OrderFilterChainDefinition appFilterChainDefinition() {
        return new OrderFilterChainDefinition()
                .addAnon("/m/login", "/m/login/bind");
    }
}
