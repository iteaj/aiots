package com.iteaj.oauth2.config;

import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.OAuth2Config;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * create time: 2021/6/27
 *
 * @author iteaj
 * @since 1.0
 */
@ConfigurationProperties("web.oauth2")
public class OAuth2ClientProperties {

    /**
     * 授权客户端配置比如：wechat, gitee, alipay等
     */
    private Map<AuthType, OAuth2Config> client;

    /**
     * Oauth2的redirectUri
     */
    private String redirectUri = "/auth/oauth2/redirectUri";


    public String getRedirectUri() {
        return redirectUri;
    }

    public OAuth2ClientProperties setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    public Map<AuthType, OAuth2Config> getClient() {
        return client;
    }

    public OAuth2ClientProperties setClient(Map<AuthType, OAuth2Config> client) {
        this.client = client;
        return this;
    }
}
