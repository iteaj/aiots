package com.iteaj.iboot.plugin.iotview;

import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.framework.spi.iot.view.ViewApi;
import com.iteaj.framework.spi.iot.view.ViewApiManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@MapperScan("com.iteaj.iboot.plugin.iotview.mapper")
@ComponentScan("com.iteaj.iboot.plugin.iotview")
public class IotViewAutoConfiguration implements ApplicationReadyListener, WebMvcConfigurer {

    /**
     * 开发环境不需要授权
     * @return
     */
    @Bean
    @Profile("dev")
    public OrderFilterChainDefinition devIsvDefinition() {
        return new OrderFilterChainDefinition().addAnon("/isv/**", "/usv/**");
    }

    /**
     * 其他环境授权配置
     * @return
     */
    @Bean
    @Profile({"prod", "test"})
    public OrderFilterChainDefinition prodIsvDefinition() {
        return new OrderFilterChainDefinition().addAnon("/isv/data/**", "/usv/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/usv/**").addResourceLocations("classpath:/isv/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/usv").setViewName("index.html");
    }

    @Override
    public void started(ApplicationContext context) {
        context.getBeanProvider(ViewApi.class)
                .orderedStream()
                .forEach(ViewApiManager::initAndRegister);
    }
}
