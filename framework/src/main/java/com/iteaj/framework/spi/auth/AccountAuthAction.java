package com.iteaj.framework.spi.auth;

import com.iteaj.framework.autoconfigure.FrameworkWebProperties;
import com.iteaj.util.module.mvc.HttpResult;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/25
 *  用于用户名和密码授权的认证动作
 * @author iteaj
 * @since 1.0
 */
public abstract class AccountAuthAction implements WebAuthAction<AuthContext> {

    private String[] urlPattern;
    private FrameworkWebProperties config;

    public AccountAuthAction(String[] urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public String[] getUrlPattern() {
        return this.urlPattern;
    }

    /**
     * 登录地址
     * @return
     */
    @Override
    public abstract String getLoginUrl();

    /**
     * 登录成功之后的回调
     * @return
     */
    @Override
    public abstract String getSuccessUrl();

    @Override
    public boolean preAuthorize(HttpServletRequest request, HttpServletResponse response) {
        return !isLoginRequest(request);
    }

    @Override
    public AuthContext getContext(HttpServletRequest request, HttpServletResponse response) {
        return AuthContext.build(AuthType.Account);
    }

    @Override
    public boolean postAuthorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(this.isAjax(request)) {
            this.writeJson(HttpResult.StatusCode("unauthorized", HttpServletResponse.SC_UNAUTHORIZED), response);
        } else {
            response.sendRedirect(this.getLoginUrl());
        }

        return true; // 拒绝放行
    }

    /**
     * 基于用户名和密码登录
     * @param request
     * @param response
     */
    @Override
    public abstract AuthToken login(AuthContext t, HttpServletRequest request, HttpServletResponse response);

    public AccountAuthAction setUrlPattern(String... urlPattern) {
        this.urlPattern = urlPattern;
        return this;
    }

    public FrameworkWebProperties getConfig() {
        return config;
    }

    @Autowired
    public AccountAuthAction setConfig(FrameworkWebProperties config) {
        this.config = config;
        return this;
    }
}
