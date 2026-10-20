package com.iteaj.iboot.plugin.social.autoconfig;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.iteaj.framework.ProfilesInclude;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 微信开发插件自动配置类
 * @author iteaj
 */
@AutoConfiguration
@ProfilesInclude("wechat")
@EnableConfigurationProperties(WechatProperties.class)
public class WechatAutoConfiguration {

    /**
     * 公众号配置存储和服务（只有在配置了 wechat.mp.app-id 时启用）
     */
    @Configuration
    @ConditionalOnProperty(prefix = "wechat.mp", name = "app-id")
    static class MpBeans {

        @Bean
        public WxMpConfigStorage wxMpConfigStorage(WechatProperties properties) {
            WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
            config.setAppId(properties.getMp().getAppId());
            config.setSecret(properties.getMp().getAppSecret());
            config.setToken(properties.getMp().getToken());
            config.setAesKey(properties.getMp().getAesKey());
            return config;
        }

        @Bean
        public WxMpService wxMpService(WxMpConfigStorage configStorage) {
            WxMpService wxMpService = new WxMpServiceImpl();
            wxMpService.setWxMpConfigStorage(configStorage);
            return wxMpService;
        }
    }

    /**
     * 小程序配置和服务（只有在配置了 wechat.ma.app-id 时启用）
     */
    @Configuration
    @ConditionalOnProperty(prefix = "wechat.ma", name = "app-id")
    static class MaBeans {

        @Bean
        public WxMaConfig wxMaConfig(WechatProperties properties) {
            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
            config.setAppid(properties.getMa().getAppId());
            config.setSecret(properties.getMa().getAppSecret());
            return config;
        }

        @Bean
        public WxMaService wxMaService(WxMaConfig config) {
            WxMaService wxMaService = new WxMaServiceImpl();
            wxMaService.setWxMaConfig(config);
            return wxMaService;
        }
    }

    /**
     * 微信开放接口无需认证（根据配置动态添加路径）
     */
    @Bean
    @Order(value = 15)
    public OrderFilterChainDefinition wechatFilterChainDefinition(WechatProperties properties) {
        OrderFilterChainDefinition def = new OrderFilterChainDefinition();

        if (properties.getMp().getAppId() != null) {
            def.addAnon("/wx/mp/callback", "/wx/mp/webhook", "/wx/mp/bind/**", "/wx/mp/admin/login/**");
        }
        if (properties.getMa().getAppId() != null) {
            def.addAnon("/wx/ma/**");
        }
        return def;
    }
}
