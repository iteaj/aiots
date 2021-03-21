package com.iteaj.security.shiro.online;

import com.iteaj.framework.spi.admin.AdminConsts;
import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;

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
        long timeToLiveSeconds = this.properties.getAdmin().getTimeToLiveSeconds();
        this.setGlobalSessionTimeout(timeToLiveSeconds * 1000);
    }

    /**
     * 1. 先从cookie获取sessionId
     * 2. 如果是移动端或者是平板 尝试从header获取access_token作为sessionId
     * @param request
     * @param response
     * @return
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        HttpServletRequest servletRequest = (HttpServletRequest)request;
        UserAgent userAgent = UserAgent.parseUserAgentString(servletRequest.getHeader("user-agent"));

        servletRequest.setAttribute(AdminConsts.USER_AGENT, userAgent);

        // cookie中没有， 如果是原生App 尝试从header获取access_token作为sessionId
        Browser browser = userAgent.getBrowser();
        if(browser != null) {
            BrowserType browserType = browser.getBrowserType();
            FrameworkWebProperties.Admin admin = properties.getAdmin();

            if(browserType == BrowserType.APP ||
                    browserType == BrowserType.MOBILE_BROWSER ||
                    browserType == BrowserType.WEB_BROWSER) {

                // 先从请求头里面获取token, 作为sessionId
                String accessToken = servletRequest.getHeader(admin.getTokenName());
                if (accessToken != null) {
                    return accessToken;
                }
            }
        }

        // 从cookie中获取
        return super.getSessionId(request, response);
    }
}
