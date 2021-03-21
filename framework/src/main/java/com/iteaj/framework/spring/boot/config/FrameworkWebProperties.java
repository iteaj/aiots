package com.iteaj.framework.spring.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "izone.web")
public class FrameworkWebProperties {

    /**
     * 后台管理端配置信息
     */
    private Admin admin;

    /**
     * 文件上传的跟目录
     * @see #uploadRootUri 此uri将映射到此目录
     * 注：只有 uploadRootDir 和 uploadRootDir 都不为空才会生效
     */
    private String uploadRootDir;
    /**
     * 上传的文件访问的路径前缀
     * @see #uploadRootDir 此访问路径将映射到此目录
     */
    private String uploadRootUri;

    public static class Admin {

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

        public void setLoginUrl(String loginUrl) {
            this.loginUrl = loginUrl;
        }

        public String getCaptchaType() {
            return captchaType;
        }

        public void setCaptchaType(String captchaType) {
            this.captchaType = captchaType;
        }

        public boolean isCaptchaEnabled() {
            return captchaEnabled;
        }

        public void setCaptchaEnabled(boolean captchaEnabled) {
            this.captchaEnabled = captchaEnabled;
        }

        public String getSuccessUrl() {
            return successUrl;
        }

        public void setSuccessUrl(String successUrl) {
            this.successUrl = successUrl;
        }

        public String getUnauthorizedUrl() {
            return unauthorizedUrl;
        }

        public void setUnauthorizedUrl(String unauthorizedUrl) {
            this.unauthorizedUrl = unauthorizedUrl;
        }

        public boolean isCacheEnable() {
            return cacheEnable;
        }

        public Admin setCacheEnable(boolean cacheEnable) {
            this.cacheEnable = cacheEnable;
            return this;
        }

        public String getTokenName() {
            return tokenName;
        }

        public Admin setTokenName(String tokenName) {
            this.tokenName = tokenName;
            return this;
        }

        public String getTokenCacheName() {
            return tokenCacheName;
        }

        public Admin setTokenCacheName(String tokenCacheName) {
            this.tokenCacheName = tokenCacheName;
            return this;
        }

        public String getAuthCacheName() {
            return authCacheName;
        }

        public Admin setAuthCacheName(String authCacheName) {
            this.authCacheName = authCacheName;
            return this;
        }

        public String getAuthorCacheName() {
            return authorCacheName;
        }

        public Admin setAuthorCacheName(String authorCacheName) {
            this.authorCacheName = authorCacheName;
            return this;
        }

        public long getTimeToLiveSeconds() {
            return timeToLiveSeconds;
        }

        public Admin setTimeToLiveSeconds(long timeToLiveSeconds) {
            this.timeToLiveSeconds = timeToLiveSeconds;
            return this;
        }

        public long getTimeToIdleSeconds() {
            return timeToIdleSeconds;
        }

        public Admin setTimeToIdleSeconds(long timeToIdleSeconds) {
            this.timeToIdleSeconds = timeToIdleSeconds;
            return this;
        }
    }

    public Admin getAdmin() {
        return admin;
    }

    public FrameworkWebProperties setAdmin(Admin admin) {
        this.admin = admin;
        return this;
    }

    public String getUploadRootDir() {
        return uploadRootDir;
    }

    public String getUploadRootUri() {
        return uploadRootUri;
    }

    public FrameworkWebProperties setUploadRootUri(String uploadRootUri) {
        this.uploadRootUri = uploadRootUri;
        return this;
    }

    public FrameworkWebProperties setUploadRootDir(String uploadRootDir) {
        this.uploadRootDir = uploadRootDir;
        return this;
    }

}
