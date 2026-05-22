package com.iteaj.iboot.plugin.wechat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * 微信开发配置属性
 * @author iteaj
 */
@Data
@ConfigurationProperties(prefix = "wechat")
public class WechatProperties {

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

    @Data
    public static class MaConfig {
        /** 小程序appId */
        private String appId;
        /** 小程序appSecret */
        private String appSecret;
    }
}
