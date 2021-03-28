package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
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
public abstract class AccountAuthAction extends AuthCallAction<AuthContext> implements WebAuthAction<AuthContext> {

    private String[] urlPattern;
    private FrameworkProperties config;

    public AccountAuthAction(String... urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public String[] getUrlPattern() {
        return this.urlPattern;
    }

    /**
     * 获取登录地址(包含登录页地址和提交地址)
     * 登录页地址在前, 提交登录地址在后 [登录页url, 提交url]
     * @return
     */
    protected abstract String[] getLoginUrl();

    @Override
    public AuthContext authorize(HttpServletRequest request, HttpServletResponse response) {
        if(isAjax(request)) {
            // 设置没有认证
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // 写出结果
            writeJson(HttpResult.StatusCode("UNAUTHORIZED"
                    , HttpServletResponse.SC_UNAUTHORIZED), response);
        } else { // 非ajax请求授权
            nonAjaxAuthorize(request, response);
        }

        // 返回null则说明不用继续执行
        return null;
    }

    /**
     * 非Ajax请求授权
     * @param request
     * @param response
     * @return
     */
    protected void nonAjaxAuthorize(HttpServletRequest request, HttpServletResponse response) {
        String[] loginUrl = this.getLoginUrl();
        try {
            if(loginUrl != null && loginUrl.length > 0) {
                response.sendRedirect(loginUrl[0]);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (IOException e) {
            throw new AuthException("重定向到登录也地址失败["+loginUrl+"]", e);
        }
    }

    @Override
    public boolean isLoginRequest(String requestUri, HttpServletRequest request) {
        final String[] loginUrl = getLoginUrl();
        if(loginUrl != null && loginUrl.length > 0) {
            requestUri = requestUri.substring(request.getContextPath().length());

            for (String url : loginUrl) {
                if(url.equalsIgnoreCase(requestUri)) {
                    return true;
                }
            }
        }

        return false;
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

    public AccountAuthAction setUrlPattern(String... urlPattern) {
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
