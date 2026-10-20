package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spi.event.FrameworkEvent;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/31
 *  注销事件
 * @author iteaj
 * @since 1.0
 */
public class LogoutEvent implements FrameworkEvent {

    private HttpServletRequest request;
    private HttpServletResponse response;

    public LogoutEvent(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public HttpServletRequest getSource() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
