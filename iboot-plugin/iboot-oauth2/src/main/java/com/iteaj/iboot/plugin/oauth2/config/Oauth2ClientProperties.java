package com.iteaj.iboot.plugin.oauth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 第三方 OAuth2 授权登录配置(本系统作为客户端接入 gitee 等平台)
 * <p>仅含授权环节所需的应用凭据与回调地址; 登录成功回跳地址、默认角色等业务侧配置由 core 直接读取同名前缀属性。</p>
 *
 * @author iteaj
 */
@Data
@ConfigurationProperties(prefix = "oauth2.client")
public class Oauth2ClientProperties {

    /** 授权回调地址(须与第三方应用注册的回调地址完全一致) */
    private String redirectUri;

    /** gitee 授权配置 */
    private Platform gitee = new Platform();

    @Data
    public static class Platform {
        /** 第三方应用 clientId */
        private String clientId;
        /** 第三方应用 clientSecret */
        private String clientSecret;
    }
}
