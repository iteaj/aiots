package com.iteaj.framework.spring.boot.config;

/**
 * create time: 2021/3/27
 *  认证相关配置信息
 * @author iteaj
 * @since 1.0
 */
public class AuthConfig {

    private String logoutUri = "/auth/logout";

    public String getLogoutUri() {
        return logoutUri;
    }

    public AuthConfig setLogoutUri(String logoutUri) {
        this.logoutUri = logoutUri;
        return this;
    }
}
