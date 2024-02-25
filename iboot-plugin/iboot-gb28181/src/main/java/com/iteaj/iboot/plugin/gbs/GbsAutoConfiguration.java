package com.iteaj.iboot.plugin.gbs;

import com.iteaj.framework.spi.admin.Module;
import com.iteaj.iboot.plugin.gbs.rtvs.RtvsDeviceServiceImpl;
import com.iteaj.iboot.plugin.gbs.rtvs.RtvsProperties;
import com.iteaj.iboot.plugin.gbs.service.GbsDeviceService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@ComponentScan({"com.iteaj.iboot.plugin.gbs"})
@MapperScan({"com.iteaj.iboot.plugin.gbs.mapper", "com.iteaj.iboot.plugin.gbs.rtvs"})
public class GbsAutoConfiguration {

    /**
     * 视频监控模块
     * @return
     */
    @Bean
    public Module gbsModule() {
        return Module.module("gbs", "视频监控", 55555);
    }

    /**
     * rtvs流媒体+gbsip实现
     */
    @Profile("rtvs")
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(RtvsProperties.class)
    public static class RtvsAutoConfiguration {

        @Bean
        public GbsDeviceService gbsDeviceService() {
            return new RtvsDeviceServiceImpl();
        }
    }
}
