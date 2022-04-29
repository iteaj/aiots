package com.iteaj.msn.lcd;

import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.DefaultModule;
import com.iteaj.framework.spi.admin.Module;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan({"com.iteaj.msn.lcd"})
@MapperScan({"com.iteaj.msn.lcd.mapper"})
@EnableConfigurationProperties(LowCodeProperties.class)
@PropertySource("classpath:application-lcg.properties")
public class LowCodeGenAutoConfiguration implements InitializingBean, WebMvcConfigurer {

    /**
     * 低代码模块无需认证授权
     * @return
     */
    @Bean
    @Order(value = 28)
    public OrderFilterChainDefinition lgcFilterChainDefinition() {
        return new OrderFilterChainDefinition()
                .addPathDefinition("/lcd/**", "anon")
                .addPathDefinition("/lib/**", "anon")
                .addPathDefinition("/assets/**", "anon");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(
                "****************************************************************\r\n" +
                "*                         启用低代码设计插件                       *\r\n" +
                "****************************************************************\r" );
    }
}
