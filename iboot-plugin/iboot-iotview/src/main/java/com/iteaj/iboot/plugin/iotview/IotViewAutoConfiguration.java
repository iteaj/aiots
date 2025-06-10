package com.iteaj.iboot.plugin.iotview;

import com.iteaj.framework.security.OrderFilterChainDefinition;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@MapperScan("com.iteaj.iboot.plugin.iotview.mapper")
@ComponentScan("com.iteaj.iboot.plugin.iotview")
public class IotViewAutoConfiguration {

    /**
     * 开发环境不需要授权
     * @return
     */
    @Bean
    @Profile("dev")
    public OrderFilterChainDefinition devIsvDefinition() {
        return new OrderFilterChainDefinition().addAnon("/isv/**");
    }

    /**
     * 其他环境授权配置
     * @return
     */
    @Bean
    @Profile({"prod", "test"})
    public OrderFilterChainDefinition prodIsvDefinition() {
        return new OrderFilterChainDefinition().addAnon("/isv/data/**");
    }
}
