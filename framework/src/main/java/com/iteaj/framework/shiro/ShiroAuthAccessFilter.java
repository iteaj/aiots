package com.iteaj.framework.shiro;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.AuthException;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.iteaj.framework.consts.CoreConst.AUTH_STATUS;

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

        final WebAuthAction authAction = handler.matcher(servletRequest);
        if(authAction == null) {
            return true; // 没有找到处理此次请求的动作, 直接放行

            //此请求无需授权, 直接放行
        } else if(!authAction.preAuthorize(servletRequest, servletResponse)) {
            return true;
        }

        /**
         * 表单的登录接口不能直接放行, 而是执行授权动作, 有授权动作判断是否调用登录方法
         * @see #onAccessDenied(ServletRequest, ServletResponse) 调用授权动作
         * @see WebAuthAction#login(Object, HttpServletRequest, HttpServletResponse) 登录方法
         */
        if(handler.isSubmitRequest(servletRequest)) {
            return false;
        }

        /**
         * 获取登录页面的请求, 直接放行
         */
        if(handler.isLoginRequest(servletRequest)) {
            return true;
        }

        /**
         * true 说明已经认证, 直接放行
         * false 说明还没有认证 交由认证动作{@link WebAuthAction}去判断是否要放心{@link #onAccessDenied(ServletRequest, ServletResponse)}
         */
        return SecurityUtils.getSubject().isAuthenticated();
    }

    /**
     * 1. 处理未认证的用户的请求
     * 2. 处理表单登录提交请求
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final HttpServletResponse servletResponse = (HttpServletResponse) response;

        // 将access_token写入到响应头
        // 如果使用token认证, 那后面客户端所有的请求头必须携带上此access_token
        HttpSession session = (servletRequest).getSession();
        (servletResponse).setHeader("access_token", session.getId());

        // 获取匹配的动作
        final WebAuthAction action = this.handler.matcher(servletRequest);

        /**
         * 获取当前请求使用的授权上下文信息, 包含获取授权类型 如下
         * @see AuthContext#getType()
         */
        AuthContext context = action.authorize(servletRequest, servletResponse);

        if(context == null || context.getType() == null) {
            throw new AuthException("认证类型必填[AuthType]");
        } else {
            // 此次必须保存授权动作到上下文
            context.setAction(action.getName());
        }

        // 1. 表单登录请求 直接调用登录方法进行登录
        if(action.isSubmitRequest(servletRequest)) {
            try {

                handler.login(context, servletRequest, servletResponse);
                boolean loginCall = action.loginCall(null, servletRequest, servletResponse);
                session.setAttribute(CoreConst.HANDLE_LOGIN_ACTION, action.getName());

                return loginCall;
            } catch (Exception e) {
                return action.loginCall(e, servletRequest, servletResponse);
            }
        } else { // 2. 未认证
            request.setAttribute(AUTH_STATUS, false);
        }

        // 继续授权
        return !handler.postAuthorize(context, servletRequest, servletResponse);

    }
}
