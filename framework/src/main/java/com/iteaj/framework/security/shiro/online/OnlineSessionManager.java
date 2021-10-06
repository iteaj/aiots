package com.iteaj.framework.security.shiro.online;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.security.shiro.ShiroUtil;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

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

    private FrameworkProperties properties;

    public OnlineSessionManager(FrameworkProperties frameworkProperties) {
        this.properties = frameworkProperties;
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

        // 尝试从header获取access_token作为sessionId
        Browser browser = userAgent.getBrowser();
        if(browser != null && browser.getBrowserType() == BrowserType.APP) {
            String tokenName = properties.getAuth().getTokenName();
            // 从请求头里面获取token, 作为sessionId
            return servletRequest.getHeader(tokenName);
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
