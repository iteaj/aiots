package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spring.boot.config.FrameworkProperties;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * create time: 2021/3/25
 *  用于处理系统的认证授权服务
 * @author iteaj
 * @since 1.0
 */
public class FrameworkAuthServlet extends GenericServlet {

    private WebAuthHandler handler;

    public FrameworkAuthServlet(WebAuthHandler handler) {
        this.handler = handler;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 去除请求上下文路径
        String requestURI = request.getRequestURI();
        requestURI = requestURI.substring(request.getContextPath().length());

        FrameworkProperties config = handler.getConfig();
        final FrameworkProperties.Oauth2 oauth2 = config.getOauth2();

        // Oauth2 redirectUri处理
        if(Objects.equals(requestURI, oauth2.getRedirectUri())) {
            handler.login((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        } else if(Objects.equals(requestURI, config.getAuth().getLogoutUri())) {
            handler.logout(request, (HttpServletResponse) servletResponse);
        }
    }
}
