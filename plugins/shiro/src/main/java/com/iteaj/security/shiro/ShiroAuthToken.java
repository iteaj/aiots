package com.iteaj.security.shiro;

import com.iteaj.framework.spi.auth.AuthToken;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * create time: 2020/3/29
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroAuthToken extends UsernamePasswordToken implements AuthToken {

    private String captcha;

    public ShiroAuthToken() { }

    public ShiroAuthToken(String username, String password, String captcha) {
        super(username, password);
        this.captcha = captcha;
    }

    public ShiroAuthToken(String username, String password, boolean rememberMe, String captcha) {
        super(username, password, rememberMe);
        this.captcha = captcha;
    }

    public ShiroAuthToken(String username, String password, boolean rememberMe, String host, String captcha) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
    }

    public String getCaptcha() {
        return captcha;
    }

    public ShiroAuthToken setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    @Override
    public String getPrincipal() {
        return this.getUsername();
    }

    @Override
    public char[] getCredentials() {
        return (char[]) super.getCredentials();
    }
}
