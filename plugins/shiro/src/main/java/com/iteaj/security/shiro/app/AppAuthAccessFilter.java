package com.iteaj.security.shiro.app;

import com.iteaj.framework.spi.app.AppAuthHandler;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * create time: 2021/3/24
 *  用来过滤app端请求
 * @author iteaj
 * @since 1.0
 */
public class AppAuthAccessFilter extends AccessControlFilter {

    private AppAuthHandler appAuthHandler;

    public AppAuthAccessFilter(AppAuthHandler appAuthHandler) {
        this.appAuthHandler = appAuthHandler;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        // 认证过的用户直接放行, 没有认证的用户将执行认证处理
        return SecurityUtils.getSubject().isAuthenticated();
    }

    /**
     * 用来处理的未认证的用户的请求
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        // 将access_token写入到响应头
        // 后面客户端所有的请求头必须携带上此access_token
        HttpSession session = ((HttpServletRequest)request).getSession();
        ((HttpServletResponse)response).setHeader("access_token", session.getId());

        /**
         * 1. 如果不允许访问说明还未授权
         * 2. 调用此处理器进行授权
         */
        appAuthHandler.handle((HttpServletRequest) request, (HttpServletResponse) response);
        return false;
    }
}
