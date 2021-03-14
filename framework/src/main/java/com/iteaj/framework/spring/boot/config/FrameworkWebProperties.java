package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.authority.AuthType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "izb")
public class FrameworkWebProperties {

    private Auth auth;

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

    public static class Auth {

        /**登录页面的Url*/
        private String loginUrl;
        /** 认证成功后要跳转的Url*/
        private String successUrl;
        /** 认证失败后要跳转的Url */
        private String unauthorizedUrl;


        /** 验证码类型, 默认数字相加 e.g. 3+3*/
        private String captchaType = "math";
        /** 是否开启验证码 */
        private boolean captchaEnabled = true;

        /**app 放在header里面的token名称*/
        private String appTokenKey ="access_token";

        /**认证类型*/
        private AuthType type = AuthType.shiro;

        public AuthType getType() {
            return type;
        }

        public Auth setType(AuthType type) {
            this.type = type;
            return this;
        }

        public String getAppTokenKey() {
            return appTokenKey;
        }

        public Auth setAppTokenKey(String appTokenKey) {
            this.appTokenKey = appTokenKey;
            return this;
        }

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
    }

    public Auth getAuth() {
        return auth;
    }

    public FrameworkWebProperties setAuth(Auth auth) {
        this.auth = auth;
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
