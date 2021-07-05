package com.iteaj.framework.shiro.online;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.autoconfigure.FrameworkWebProperties;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.utils.ShiroUtil;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineSessionManager extends DefaultWebSessionManager {

    private FrameworkWebProperties properties;
    private Logger logger = LoggerFactory.getLogger(getClass());
    public OnlineSessionManager(FrameworkWebProperties frameworkWebProperties) {
        this.properties = frameworkWebProperties;
    }

    /**
     * 1. 尝试从header获取access_token作为sessionId
     * 2. 如果没有则从cookie获取
     * @param request
     * @param response
     * @return
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        HttpServletRequest servletRequest = (HttpServletRequest)request;
        UserAgent userAgent = UserAgent.parseUserAgentString(servletRequest.getHeader("user-agent"));

        servletRequest.setAttribute(CoreConst.WEB_USER_AGENT, userAgent);
        final Object action = request.getAttribute(CoreConst.HANDLE_AUTH_ACTION);
        if(action instanceof WebAuthAction) {
            final String tokenName = ((WebAuthAction<?>) action).getTokenName();
            if(tokenName != null) {
                final String token = servletRequest.getHeader(tokenName);
                if(logger.isDebugEnabled()) {
                    logger.debug("将使用请求头["+tokenName+"="+token+"]作为sessionId进行授权");
                }

                return token;
            }
        }

        // 从cookie中获取
        return super.getSessionId(request, response);
    }



    @Override
    public boolean isServletContainerSessions() {
        return false;
    }

    @Override
    protected void applyGlobalSessionTimeout(Session session) {
        final WebAuthAction action = ShiroUtil.getRequestAttr(CoreConst.HANDLE_AUTH_ACTION);
        if(action != null) {
            session.setTimeout(TimeUnit.SECONDS.toMillis(action.expireTime()));
            onChange(session);
        } else {
            super.applyGlobalSessionTimeout(session);
        }
    }
}
