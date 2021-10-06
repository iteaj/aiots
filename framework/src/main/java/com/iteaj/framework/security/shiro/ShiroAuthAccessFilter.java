package com.iteaj.framework.security.shiro;

import com.iteaj.framework.spi.auth.WebAuthHandler;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.iteaj.framework.consts.CoreConst.EXEC_EXCEPTION_STATUS;

/**
 * create time: 2021/3/24
 *  用来过滤框架所有请求
 * @author iteaj
 * @since 1.0
 */
public class ShiroAuthAccessFilter extends AccessControlFilter {

    private WebAuthHandler handler;
    protected static Logger logger = LoggerFactory.getLogger(WebAuthHandler.class);

    public ShiroAuthAccessFilter(WebAuthHandler webAuthHandler) {
        this.handler = webAuthHandler;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final HttpServletResponse servletResponse = (HttpServletResponse) response;

        //校验请求是否需要认证
        if(!handler.preAuthorize(servletRequest, servletResponse)) {
            return true;
        }

        /**
         * true 说明已经认证, 直接放行
         */
        final boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
        if(authenticated) {
            return true;
        } else { // 未认证, 拒绝放行
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return !handler.postAuthorize((HttpServletRequest) request, (HttpServletResponse) response);
    }
}
