package com.iteaj.iboot.plugin.wechat.autoconfig;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.iteaj.framework.security.OrderFilterChainDefinition;
import com.iteaj.iboot.plugin.wechat.config.WechatProperties;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 微信开发插件自动配置类
 * @author iteaj
 */
@AutoConfiguration
@ComponentScan({"com.iteaj.iboot.plugin.wechat"})
@MapperScan({"com.iteaj.iboot.plugin.wechat.mapper"})
@EnableConfigurationProperties(WechatProperties.class)
public class WechatAutoConfiguration {

    /**
     * 公众号配置存储
     */
    @Bean
    public WxMpConfigStorage wxMpConfigStorage(WechatProperties properties) {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(properties.getMp().getAppId());
        config.setSecret(properties.getMp().getAppSecret());
        config.setToken(properties.getMp().getToken());
        config.setAesKey(properties.getMp().getAesKey());
        return config;
    }

    /**
     * 公众号服务
     */
    @Bean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(configStorage);
        return wxMpService;
    }

    /**
     * 小程序配置
     */
    @Bean
    public WxMaConfig wxMaConfig(WechatProperties properties) {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getMa().getAppId());
        config.setSecret(properties.getMa().getAppSecret());
        return config;
    }

    /**
     * 小程序服务
     */
    @Bean
    public WxMaService wxMaService(WxMaConfig config) {
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(config);
        return wxMaService;
    }

    /**
     * 微信开放接口无需认证（授权回调、消息推送、绑定页面）
     */
    @Bean
    @Order(value = 15)
    public OrderFilterChainDefinition wechatFilterChainDefinition() {
        return new OrderFilterChainDefinition()
                .addAnon("/wechat/mp/callback", "/wechat/mp/webhook", "/wechat/mp/bind/**"
                        , "/wechat/ma/**", "/m/login");
    }
}
