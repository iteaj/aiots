package com.iteaj.security.shiro.online;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
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

    private FrameworkProperties properties;

    public OnlineSessionManager(FrameworkProperties frameworkProperties) {
        this.properties = frameworkProperties;
        long timeToLiveSeconds = this.properties.getAdmin().getTimeToLiveSeconds();
        this.setGlobalSessionTimeout(timeToLiveSeconds * 1000);
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

        // cookie中没有， 如果是原生App 尝试从header获取access_token作为sessionId
        Browser browser = userAgent.getBrowser();
        if(browser != null && browser.getBrowserType() == BrowserType.APP) {
            String tokenName = properties.getApp().getTokenName();
            // 从请求头里面获取token, 作为sessionId
            return servletRequest.getHeader(tokenName);
        }

        // 从cookie中获取
        return super.getSessionId(request, response);
    }
}
