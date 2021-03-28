package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spi.app.AppAuthAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/28
 *
 * @author iteaj
 * @since 1.0
 */
public abstract class ExceptionAction<T> implements AppAuthAction<T> {

    /**
     * 登录异常处理
     * @param cause
     * @param request
     * @param response
     */
    public void loginCause(Exception cause, HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * 注销异常处理
     * @param cause
     * @param request
     * @param response
     */
    public void logoutCause(Exception cause, HttpServletRequest request, HttpServletResponse response) {

    }
}
