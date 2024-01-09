package com.iteaj.iboot.plugin.gbs;

import com.genersoft.iot.vmp.GbsWvpAutoConfiguration;
import com.iteaj.framework.spi.admin.Module;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.*;

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
     * 流媒体ZLMedia + gbsip(wvp)实现
     */
    @Profile("wvp")
    @Configuration(proxyBeanMethods = false)
    public static class WvpAutoConfiguration extends GbsWvpAutoConfiguration {

    }

    /**
     * rtvs流媒体+gbsip实现
     */
    @Profile("rtvs")
    @Configuration(proxyBeanMethods = false)
    @ComponentScan({"com.iteaj.iboot.plugin.gbs"})
    @MapperScan("com.iteaj.iboot.plugin.gbs.mapper")
    public static class RtvsAutoConfiguration {

    }
}
