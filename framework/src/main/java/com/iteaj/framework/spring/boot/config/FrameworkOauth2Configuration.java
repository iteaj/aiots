package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.AbstractOAuth2Handler;
import com.iteaj.framework.spi.oauth2.OAuth2Config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import java.util.Map;

/**
 * create time: 2021/3/28
 *
 * @author iteaj
 * @since 1.0
 */
@ConditionalOnBean(AbstractOAuth2Handler.class)
public class FrameworkOauth2Configuration {

    @Getter
    @Setter
    public static class OAuth2Properties {
        /**
         * Oauth2的redirectUri
         */
        private String redirectUri = "/auth/oauth2/redirectUri";

        /**
         * 授权客户端配置比如：wechat, gitee, alipay等
         */
        private Map<AuthType, OAuth2Config> client;
    }
}
