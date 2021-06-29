package com.iteaj.framework.shiro;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
        final WebAuthAction authAction = handler.matcher(servletRequest);
        if(authAction == null) {
            return true; // 没有找到处理此次请求的动作, 直接放行
        }

        boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
        if(handler.isSubmitRequest(servletRequest)) {
            return false; // 查找登录页请求
        }

        if(handler.isLoginRequest(servletRequest)) {
            return true; // 表单登录请求
        }

        return authenticated;
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
        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final HttpServletResponse servletResponse = (HttpServletResponse) response;
        // 将access_token写入到响应头
        // 如果使用token认证, 那后面客户端所有的请求头必须携带上此access_token
        HttpSession session = (servletRequest).getSession();
        (servletResponse).setHeader("access_token", session.getId());

        // 获取匹配的动作
        final WebAuthAction action = this.handler.matcher(servletRequest);

        if(action != null) {

            // 授权前校验此请求是否需要授权
            if (!action.preAuthorize(servletRequest, servletResponse)) {
                return true;
            }

            // 执行授权获取授权上下文
            AuthContext context = action.authorize(servletRequest, servletResponse);

            if(context == null || context.getType() == null) {
                throw new AuthException("认证类型必填[AuthType]");
            } else {
                // 此次必须保存授权动作
                context.setAction(action.getName());
            }

            if(action.isSubmitRequest(servletRequest)) {
                try {

                    handler.login(context, servletRequest, servletResponse);
                    boolean loginCall = action.loginCall(null, servletRequest, servletResponse);
                    session.setAttribute(CoreConst.HANDLE_LOGIN_ACTION, action.getName());

                    return loginCall;
                } catch (Exception e) {
                    return action.loginCall(e, servletRequest, servletResponse);
                }

            }

            // 继续授权
            return !handler.postAuthorize(context, servletRequest, servletResponse);
        } else {

            if(logger.isTraceEnabled()) {
                logger.trace("此次认证请求[{}]没有匹配到处理动作[WebAuthAction] 直接放行"
                        , servletRequest.getRequestURI());
            }

            return true;
        }

    }
}
