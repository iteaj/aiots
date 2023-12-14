package com.genersoft.iot.vmp;

import com.genersoft.iot.vmp.conf.MediaConfig;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.framework.spi.admin.Module;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@ComponentScan({"com.genersoft.iot.vmp"})
@ServletComponentScan("com.genersoft.iot.vmp.conf")
@MapperScan("com.genersoft.iot.vmp.storager.dao")
@PropertySource("classpath:application-wvp.yml")
@EnableConfigurationProperties(MediaConfig.class)
public class GbSipAutoConfiguration {

    /**
     * 视频监控模块
     * @return
     */
    @Bean
    public Module wvpModule() {
        return Module.module("gbs", "视频监控", 55555);
    }

    /**
     * @return
     */
    @Bean
    @Order(value = 18)
    public OrderFilterChainDefinition gbsFilterChainDefinition() {
        return new OrderFilterChainDefinition().addAnon("/gbs/zlm/hook/**");
    }
}
