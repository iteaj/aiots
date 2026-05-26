package com.iteaj.iboot.plugin.wechat.autoconfig;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * 微信开发配置属性
 * @author iteaj
 */
@Data
@ConfigurationProperties(prefix = "wechat")
public class WechatProperties implements InitializingBean {

    /**
     * 公众号配置
     */
    private MpConfig mp = new MpConfig();

    /**
     * 小程序配置
     */
    private MaConfig ma = new MaConfig();

    @Data
    public static class MpConfig {
        /** 公众号appId */
        private String appId;
        /** 公众号appSecret */
        private String appSecret;
        /** 服务器配置token */
        private String token;
        /** 消息加解密密钥 */
        private String aesKey;
        /** 微信授权后跳转的绑定页URL（由移动端提供） */
        private String bindPageUrl;
        /** 绑定成功后跳转URL */
        private String successPageUrl;
    }

    /**
     * 校验配置：至少需要配置小程序或公众号的appId
     */
    @Override
    public void afterPropertiesSet() {
        if (StrUtil.isBlank(mp.appId) && StrUtil.isBlank(ma.appId)) {
            throw new IllegalStateException(
                    "微信开发配置缺失：请配置 wechat.mp.app-id（公众号）或 wechat.ma.app-id（小程序）");
        }
    }

    @Data
    public static class MaConfig {
        /** 小程序appId */
        private String appId;
        /** 小程序appSecret */
        private String appSecret;
    }
}
