package com.iteaj.iboot.plugin.shiro.online;

import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.consts.CoreConst;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.util.StringUtils;

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
        String tokenName = this.properties.getWeb().getSession().getTokenName();
        // 修改cookie的名称
        this.getSessionIdCookie().setName(tokenName);
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
        FrameworkProperties.Web web = properties.getWeb();
        if(web.getSession().isWriteHeader()) {
            String tokenName = web.getSession().getTokenName();
            String token = servletRequest.getHeader(tokenName);
            if(StringUtils.hasText(token)) {
                return token;
            }
        }

        // 从cookie中获取
        return super.getSessionId(request, response);
    }

}
