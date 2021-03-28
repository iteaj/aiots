package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.util.module.mvc.HttpResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * create time: 2021/3/28
 *  认证回调
 * @author iteaj
 * @since 1.0
 */
public abstract class AuthCallAction<T> implements WebAuthAction<T> {

    /**
     * 登录回调处理
     * @param cause 有无异常
     * @param request
     * @param response
     */
    public void loginCall(Exception cause, HttpServletRequest request, HttpServletResponse response) {
        if(cause == null) {
            writeJson(HttpResult.Success("登录成功"), response);
        } else {
            writeJson(HttpResult.Fail("登录失败"), response);
        }
    }

    /**
     * 注销回调处理
     * @param cause 有无异常
     * @param request
     * @param response
     */
    public void logoutCall(Exception cause, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(isAjax(request)) {
            writeJson(HttpResult.Success("注销成功"), response);
        } else {
            nonAjaxLogoutCall(cause, request, response);
        }
    }

    /**
     * 非ajax注销回调
     * @param cause
     * @param request
     * @param response
     */
    protected abstract void nonAjaxLogoutCall(Exception cause, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
