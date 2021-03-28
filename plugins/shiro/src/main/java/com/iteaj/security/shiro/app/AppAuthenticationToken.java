package com.iteaj.security.shiro.app;

import com.iteaj.framework.spi.auth.AuthToken;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * create time: 2021/3/23
 *
 * @author iteaj
 * @since 1.0
 */
public class AppAuthenticationToken implements AuthenticationToken, AuthToken {

    /**
     * 校验码
     */
    private String code;

    /**
     * 登录账户
     */
    private String userName;

    /**
     * 登录密码
     */
    private String password;

    public AppAuthenticationToken(String code, String userName, String password) {
        this.code = code;
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public AppAuthenticationToken setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AppAuthenticationToken setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public Object getPrincipal() {
        return this.userName;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }

    public String getCode() {
        return code;
    }

    public AppAuthenticationToken setCode(String code) {
        this.code = code;
        return this;
    }
}
