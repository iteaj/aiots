package com.iteaj.framework.spi.auth;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.autoconfigure.FrameworkWebProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected static Logger logger = LoggerFactory.getLogger(FrameworkAuthServlet.class);

    public FrameworkAuthServlet(WebAuthHandler handler) {
        this.handler = handler;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 去除请求上下文路径
        String requestURI = request.getRequestURI();
        requestURI = requestURI.substring(request.getContextPath().length());

        FrameworkWebProperties config = handler.getConfig();
        final FrameworkWebProperties.Oauth2 oauth2 = config.getOauth2();

        // Oauth2 redirectUri回调处理
        if(Objects.equals(requestURI, oauth2.getRedirectUri())) {
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
