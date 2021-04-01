package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.util.module.mvc.HttpResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

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
     * @return true 拦截此请求  false 放行此请求
     */
    public boolean loginCall(Exception cause, HttpServletRequest request, HttpServletResponse response) {
        if(cause == null) { // 登录成功
            if(isAjax(request)) {
                writeJson(HttpResult.Success(getSuccessUrl(), "登录成功"), response);
            } else {
                try {
                    response.sendRedirect(getSuccessUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else { // 登录失败
            Throwable throwable = cause;
            if(cause instanceof UndeclaredThrowableException) {
                throwable = ((UndeclaredThrowableException) cause)
                        .getUndeclaredThrowable().getCause();
            }

            if(isAjax(request)) {
                if(throwable instanceof AuthException) {
                    writeJson(HttpResult.Fail(throwable.getMessage()), response);
                } else {
                    writeJson(HttpResult.Fail("登录失败"), response);
                }
            } else {
                // 登录失败重定向到登录页面
                try {
                    response.sendRedirect(getLoginUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

}
