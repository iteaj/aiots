package com.iteaj.dev.config;

import com.iteaj.framework.authority.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.Module;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(DevelopProperties.class)
public class DevelopAutoConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    /**
     * 开放下面的路径, 不能被权限管理器拦截
     * @return
     */
    @Bean
    public OrderFilterChainDefinition developOrderFilterChain() {
        return new OrderFilterChainDefinition()
                .addPathDefinition("/dev/online", "anon");
    }

    @Bean
    public Module devModule() {
        return new Module() {
            @Override
            public int getOrder() {
                return 50;
            }

            @Override
            public String getMsn() {
                return "dev";
            }

            @Override
            public String getName() {
                return "开发模块";
            }
        };
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        event.getEnvironment().addActiveProfile("gen");
    }
}
