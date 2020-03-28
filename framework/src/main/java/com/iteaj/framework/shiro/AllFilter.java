package com.iteaj.framework.shiro;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jdk：1.8
 *  登录退出或者过时, ajax访问的处理
 * @author iteaj
 * create time：2019/6/2
 */
public class AllFilter extends UserFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean accessAllowed = super.isAccessAllowed(request, response, mappedValue);
        if(accessAllowed) return true;

        try {
            // 验证是否是Ajax请求
            if(request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                String ajaxId = ((HttpServletRequest) request).getHeader("x-requested-with");
                if(ajaxId != null && ajaxId.equalsIgnoreCase("XMLHttpRequest")) {
                    ((HttpServletResponse) response)
                            .addHeader("Authentication", getLoginUrl());

                    ((HttpServletResponse) response)
                            .sendError(HttpServletResponse.SC_UNAUTHORIZED, getLoginUrl());
                } else {
                    return super.onAccessDenied(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return false;
    }
}
