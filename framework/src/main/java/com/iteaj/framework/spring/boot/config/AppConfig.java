package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.spi.app.AuthType;
import com.iteaj.framework.spi.oauth2.OAuth2Config;

import java.util.Map;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class AppConfig {

    /**
     * OAuth2相关配置
     */
    private Oauth2 oauth2;

    /**
     * 用户名密码认证配置
     */
    private Account account = new Account();

    /**
     * app请求头里面的token名称
     */
    private String tokenName = "access_token";

    /**
     * 授权域名(不配置将自动获取)
     */
    private String domain;

    /**
     * 移动端登录地址(账号密码登录地址或Oauth2的redirectUri授权地址)
     */
    private String redirectUri = "/app/auth/login";

    /**
     * 需要过滤拦截的url
     */
    private String[] includes = new String[]{"/app/**"};

    /**
     * 不需要过滤拦截的url
     */
    private String[] excludes = new String[]{"/app/auth/login"};

    /**
     * Oauth2授权配置
     */
    public static class Oauth2 {
        /**
         * 授权客户端配置比如：wechat, gitee, alipay等
         */
        private Map<AuthType, OAuth2Config> client;

        public Map<AuthType, OAuth2Config> getClient() {
            return client;
        }

        public Oauth2 setClient(Map<AuthType, OAuth2Config> client) {
            this.client = client;
            return this;
        }

    }

    /**
     * 用户名密码授权配置
     */
    public static class Account {

        /**
         * 校验码(短信等)
         */
        private String code = "code";

        /**
         * 登录用户名
         */
        private String userName = "userName";

        /**
         * 登录密码
         */
        private String password = "password";

        public String getCode() {
            return code;
        }

        public Account setCode(String code) {
            this.code = code;
            return this;
        }

        public String getUserName() {
            return userName;
        }

        public Account setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public Account setPassword(String password) {
            this.password = password;
            return this;
        }
    }

    public Oauth2 getOauth2() {
        return oauth2;
    }

    public AppConfig setOauth2(Oauth2 oauth2) {
        this.oauth2 = oauth2;
        return this;
    }

    public Account getAccount() {
        return account;
    }

    public AppConfig setAccount(Account account) {
        this.account = account;
        return this;
    }

    public String getTokenName() {
        return tokenName;
    }

    public AppConfig setTokenName(String tokenName) {
        this.tokenName = tokenName;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public AppConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public AppConfig setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    public String[] getIncludes() {
        return includes;
    }

    public AppConfig setIncludes(String[] includes) {
        this.includes = includes;
        return this;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public AppConfig setExcludes(String[] excludes) {
        this.excludes = excludes;
        return this;
    }
}
