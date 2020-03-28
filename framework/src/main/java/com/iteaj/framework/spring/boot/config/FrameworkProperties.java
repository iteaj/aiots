package com.iteaj.framework.spring.boot.config;

import com.iteaj.framework.authority.AuthType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "izb")
public class FrameworkProperties {

    private Auth auth;

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
        /**认证类型*/
        private AuthType type = AuthType.shiro;

        public AuthType getType() {
            return type;
        }

        public Auth setType(AuthType type) {
            this.type = type;
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

    public FrameworkProperties setAuth(Auth auth) {
        this.auth = auth;
        return this;
    }
}
