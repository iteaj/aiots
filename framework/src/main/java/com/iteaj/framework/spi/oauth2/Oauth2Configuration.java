package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * create time: 2021/3/28
 *
 * @author iteaj
 * @since 1.0
 */
public class Oauth2Configuration {

    @Data
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
