package com.iteaj.security.shiro.online;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineSessionManager extends DefaultWebSessionManager {

    private FrameworkWebProperties properties;

    public OnlineSessionManager(FrameworkWebProperties frameworkWebProperties) {
        this.properties = frameworkWebProperties;
    }

    /**
     * 1. 先从cookie获取sessionId
     * 2. 如果是原生app 尝试从header获取access_token作为sessionId
     * @param request
     * @param response
     * @return
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        HttpServletRequest servletRequest = (HttpServletRequest)request;
        UserAgent userAgent = UserAgent.parseUserAgentString(servletRequest.getHeader("user-agent"));

        servletRequest.setAttribute(CoreConst.WEB_USER_AGENT, userAgent);
        // 设置请求属性
        ThreadContext.put(CoreConst.HTTP_SERVLET_REQUEST, request);

        // cookie中没有， 如果是原生App 尝试从header获取access_token作为sessionId
        Browser browser = userAgent.getBrowser();
        if(browser != null && browser.getBrowserType() == BrowserType.APP) {
            String tokenName = properties.getAuth().getTokenName();
            // 从请求头里面获取token, 作为sessionId
            return servletRequest.getHeader(tokenName);
        }

        // 从cookie中获取
        return super.getSessionId(request, response);
    }

    protected void applyGlobalSessionTimeout(Session session) {
        session.setTimeout(getGlobalSessionTimeout());
        onChange(session);
    }
}
