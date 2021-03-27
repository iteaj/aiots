package com.iteaj.framework.spi.app;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * create time: 2021/3/25
 *  用于处理App端的登录服务
 * @author iteaj
 * @since 1.0
 */
public class AppLoginServlet extends GenericServlet {

    private AppAuthHandler handler;

    public AppLoginServlet(AppAuthHandler handler) {
        this.handler = handler;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        handler.login((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
    }
}
