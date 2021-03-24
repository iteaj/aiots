package com.iteaj.security.shiro.app;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * create time: 2021/3/24
 *  用来过滤app端请求
 * @author iteaj
 * @since 1.0
 */
public class AppAccessFilter extends AccessControlFilter {


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        // 认证过的用户直接放行
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

        return false;
    }
}
