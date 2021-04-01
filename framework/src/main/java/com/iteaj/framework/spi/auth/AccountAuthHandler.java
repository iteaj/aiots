package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.util.module.mvc.HttpResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * create time: 2021/3/24
 *  普通的账号密码认证
 * @author iteaj
 * @since 1.0
 */
public class AccountAuthHandler extends DefaultAuthHandler {

    public AccountAuthHandler(List<WebAuthAction> actions) {
        super(actions);
    }

    @Override
    protected boolean doAuthorize(WebAuthAction authAction, AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 如果是登录请求直接调用登录
        if(authAction.isLoginRequest(request.getRequestURI(), request)) {
            try {
                authAction.login(null, request, response);
                return this.loginCallHandle(authAction, null, request, response);
            } catch (Exception e) {
                return this.loginCallHandle(authAction, e, request, response);
            }
        } else {
            // 非登录请求
            if(authAction.isAjax(request)) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "unauthorized");
                authAction.writeJson(HttpResult.StatusCode("unauthorized", HttpServletResponse.SC_UNAUTHORIZED), response);
            } else {
                response.sendRedirect(authAction.getLoginUrl());
            }
        }

        return true;
    }

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) {

    }
}
