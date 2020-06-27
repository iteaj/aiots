package com.iteaj.security.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jdk：1.8
 *  登录退出或者过时, ajax访问的处理
 * @author iteaj
 * create time：2019/6/2
 */
public class AjaxFilter extends AccessControlFilter implements ServletContextAware {

    private boolean isAjax = false;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            if(isLoginRequest(request, response)) return true;
            if(SecurityUtils.getSubject().isAuthenticated()) return true;

            // 验证是否是Ajax请求
            if(request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                String ajaxId = ((HttpServletRequest) request).getHeader("x-requested-with");
                if(ajaxId != null && ajaxId.equalsIgnoreCase("XMLHttpRequest")) {

                    this.isAjax = true;
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        super.setServletContext(servletContext);
    }

    @Override
    public FilterConfig getFilterConfig() {
        return new NameableFilterConfig("ajax", getServletContext());
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if(isAjax) {
            ((HttpServletResponse) response)
                    .sendError(HttpServletResponse.SC_UNAUTHORIZED, getLoginUrl());

            return isAjax = false;
        } else {
            return true;
        }
    }
}
