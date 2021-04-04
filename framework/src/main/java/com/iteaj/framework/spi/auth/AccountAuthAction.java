package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import com.iteaj.util.module.mvc.HttpResult;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @Override
    public boolean preAuthorize(HttpServletRequest request, HttpServletResponse response) {
        return !(request.getMethod().equalsIgnoreCase("GET") && isLoginRequest(request));
    }

    @Override
    public AuthContext authorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return AuthContext.build(AuthType.Account);
    }

    @Override
    public boolean postAuthorize(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 如果是登录请求直接调用登录
        // 非登录请求
        if(this.isAjax(request)) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "unauthorized");
            this.writeJson(HttpResult.StatusCode("unauthorized", HttpServletResponse.SC_UNAUTHORIZED), response);
        } else {
            response.sendRedirect(this.getLoginUrl());
        }

        return true;
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
