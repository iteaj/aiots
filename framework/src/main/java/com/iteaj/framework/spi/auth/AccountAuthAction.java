package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spi.app.AppAuthAction;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/25
 *  用于用户名和密码授权的认证动作
 * @author iteaj
 * @since 1.0
 */
public abstract class AccountAuthAction extends ExceptionAction<AuthContext> implements AppAuthAction<AuthContext> {

    private String urlPattern;
    private FrameworkProperties config;

    public AccountAuthAction(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public String getUrlPattern() {
        return this.urlPattern;
    }

    @Override
    public int getOrder() {
        return 88;
    }

    @Override
    public AuthContext authorize(HttpServletRequest request, HttpServletResponse response) {
        // 以用户名和密码的方式授权
        return AuthContext.build(AuthType.Account);
    }

    /**
     * 基于用户名和密码登录
     * @param request
     * @param response
     */
    @Override
    public abstract AuthToken login(AuthContext t, HttpServletRequest request, HttpServletResponse response);

    /**
     * 注销
     * @param request
     * @param response
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) { }

    public AccountAuthAction setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
        return this;
    }

    public FrameworkProperties getConfig() {
        return config;
    }

    @Autowired
    public AccountAuthAction setConfig(FrameworkProperties config) {
        this.config = config;
        return this;
    }
}
