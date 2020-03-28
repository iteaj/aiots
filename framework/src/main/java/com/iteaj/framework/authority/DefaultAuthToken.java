package com.iteaj.framework.authority;

/**
 * create time: 2019/12/6
 *
 * @author iteaj
 * @since 1.0
 */
public class DefaultAuthToken implements AuthToken {

    private String captcha;
    private String userName;
    private String password;
    private boolean rememberMe;

    @Override
    public Object getPrincipal() {
        return this.userName;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public DefaultAuthToken setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public DefaultAuthToken setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DefaultAuthToken setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public DefaultAuthToken setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
        return this;
    }
}
