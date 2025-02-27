package com.iteaj.iboot.plugin.le5le;

import com.iteaj.framework.ProfilesInclude;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

@ProfilesInclude("le5le")
@ComponentScan({"com.iteaj.iboot.plugin.le5le"})
@MapperScan({"com.iteaj.iboot.plugin.le5le.mapper"})
@EnableConfigurationProperties(Le5leProperties.class)
public class Le5LeAutoConfiguration {

    @Bean
    @Order(value = 18)
    public OrderFilterChainDefinition le5leFilterChainDefinition() {
        return new OrderFilterChainDefinition()
                .addAnon("/iot/le5le/api/**");
    }
}
