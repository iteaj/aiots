package com.iteaj.framework.spring.boot.config;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class AdminConfig {
    /**登录页面的Url*/
    private String loginUrl;

    /** 认证成功后要跳转的Url*/
    private String successUrl;

    /** 认证失败后要跳转的Url */
    private String unauthorizedUrl;

    /**
     * 缓存的存活时间 (默认时间30分钟)
     */
    private long timeToLiveSeconds = 30 * 60;

    /**
     * 设置对象在失效前的允许闲置时间（单位：秒）
     * 默认0 说明此属性无效(也就是可闲置时间无穷大)
     */
    private long timeToIdleSeconds = 10 * 60;

    /**
     * 是否允许缓存token(redis只缓存session)
     */
    private boolean cacheEnable;

    /**
     * 令牌的名称
     */
    private String tokenName = "access_token";

    /**
     * 令牌(token)的缓存名称
     */
    private String tokenCacheName = "izone_token_cache";

    /**
     * 认证信息的缓存名称
     */
    private String authCacheName = "izone_authentication_cache";

    /**
     * 权限信息的缓存名称
     */
    private String authorCacheName = "izone_authorization_cache";


    /** 验证码类型, 默认数字相加 e.g. 3+3*/
    private String captchaType = "math";

    /** 是否开启验证码 */
    private boolean captchaEnabled = true;

    public String getLoginUrl() {
        return loginUrl;
    }

    public AdminConfig setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
        return this;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public AdminConfig setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
        return this;
    }

    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }

    public AdminConfig setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
        return this;
    }

    public long getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public AdminConfig setTimeToLiveSeconds(long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
        return this;
    }

    public long getTimeToIdleSeconds() {
        return timeToIdleSeconds;
    }

    public AdminConfig setTimeToIdleSeconds(long timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
        return this;
    }

    public boolean isCacheEnable() {
        return cacheEnable;
    }

    public AdminConfig setCacheEnable(boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
        return this;
    }

    public String getTokenName() {
        return tokenName;
    }

    public AdminConfig setTokenName(String tokenName) {
        this.tokenName = tokenName;
        return this;
    }

    public String getTokenCacheName() {
        return tokenCacheName;
    }

    public AdminConfig setTokenCacheName(String tokenCacheName) {
        this.tokenCacheName = tokenCacheName;
        return this;
    }

    public String getAuthCacheName() {
        return authCacheName;
    }

    public AdminConfig setAuthCacheName(String authCacheName) {
        this.authCacheName = authCacheName;
        return this;
    }

    public String getAuthorCacheName() {
        return authorCacheName;
    }

    public AdminConfig setAuthorCacheName(String authorCacheName) {
        this.authorCacheName = authorCacheName;
        return this;
    }

    public String getCaptchaType() {
        return captchaType;
    }

    public AdminConfig setCaptchaType(String captchaType) {
        this.captchaType = captchaType;
        return this;
    }

    public boolean isCaptchaEnabled() {
        return captchaEnabled;
    }

    public AdminConfig setCaptchaEnabled(boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
        return this;
    }
}
