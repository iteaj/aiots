package com.iteaj.framework.security;

/**
 * create time: 2020/3/29
 *
 * @author iteaj
 * @since 1.0
 */
public class AuthzUrl {

    private String loginUrl;
    private String successUrl;
    private String unauthorizedUrl;

    public AuthzUrl(String loginUrl, String successUrl, String unauthorizedUrl) {
        this.loginUrl = loginUrl;
        this.successUrl = successUrl;
        this.unauthorizedUrl = unauthorizedUrl;
    }

    public static AuthzUrl instance() {
        return new AuthzUrl("/login", "/", "/401");
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public AuthzUrl setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
        return this;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public AuthzUrl setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
        return this;
    }

    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }

    public AuthzUrl setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
        return this;
    }
}
