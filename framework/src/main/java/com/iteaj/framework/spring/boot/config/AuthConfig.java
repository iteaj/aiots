package com.iteaj.framework.spring.boot.config;

/**
 * create time: 2021/3/27
 *  认证相关配置信息
 * @author iteaj
 * @since 1.0
 */
public class AuthConfig {

    private String loginUrl;

    private String logoutUrl;

    public String getLoginUrl() {
        return loginUrl;
    }

    public AuthConfig setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
        return this;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public AuthConfig setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
        return this;
    }
}
