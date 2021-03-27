package com.iteaj.framework.spring.boot.config;

/**
 * create time: 2021/3/27
 *  认证相关配置信息
 * @author iteaj
 * @since 1.0
 */
public class AuthConfig {

    /**
     * 移动端登录地址(账号密码登录地址或Oauth2的redirectUri授权地址)
     */
    private String loginUri = "/auth/login";

    private String logoutUri = "/auth/logout";

    /**
     * 授权域名(不配置将自动获取)
     */
    private String domain;

    /**
     * token认证时请求头的token名称
     */
    private String tokenName = "access_token";

    public String getLoginUri() {
        return loginUri;
    }

    public AuthConfig setLoginUri(String loginUri) {
        this.loginUri = loginUri;
        return this;
    }

    public String getLogoutUri() {
        return logoutUri;
    }

    public AuthConfig setLogoutUri(String logoutUri) {
        this.logoutUri = logoutUri;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public AuthConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getTokenName() {
        return tokenName;
    }

    public AuthConfig setTokenName(String tokenName) {
        this.tokenName = tokenName;
        return this;
    }
}
