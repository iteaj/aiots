package com.iteaj.oauth2.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.autoconfigure.FrameworkWebProperties;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.oauth2.config.OAuth2ClientProperties;
import org.springframework.beans.factory.annotation.Autowired;

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
public class OAuth2HandleServlet extends GenericServlet {

    private WebAuthHandler handler;
    @Autowired
    private OAuth2ClientProperties clientProperties;
    protected static Logger logger = LoggerFactory.getLogger(OAuth2HandleServlet.class);

    public OAuth2HandleServlet(WebAuthHandler handler) {
        this.handler = handler;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 去除请求上下文路径
        String requestURI = request.getRequestURI();
        requestURI = requestURI.substring(request.getContextPath().length());

        FrameworkWebProperties config = handler.getConfig();

        // Oauth2 redirectUri回调处理
        if(Objects.equals(requestURI, clientProperties.getRedirectUri())) {
            Object context = request.getSession().getAttribute(CoreConst.SESSION_AUTH_CONTEXT);
            if(context instanceof AuthContext) {
                try {
                    // 回调登录
                    handler.login((AuthContext) context, (HttpServletRequest)
                            servletRequest, (HttpServletResponse) servletResponse);

                    // 保存处理此次授权的动作
                    request.getSession().setAttribute(CoreConst
                            .HANDLE_LOGIN_ACTION, ((AuthContext) context).getAction());
                } finally {
                    // 移除上下文
                    request.getSession().removeAttribute(CoreConst.SESSION_AUTH_CONTEXT);
                }
            } else {
                logger.warn("找不到[AuthContext], 可能已释放", request.getRequestURI());
            }
        } else if(Objects.equals(requestURI, config.getAuth().getLogoutUri())) {
            try {
                handler.logout(request, (HttpServletResponse) servletResponse);
            } finally {
                request.getSession().removeAttribute(CoreConst.HANDLE_LOGIN_ACTION);
            }
        }
    }
}
