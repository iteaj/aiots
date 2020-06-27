package com.iteaj.security.shiro.online;

import com.iteaj.framework.spi.admin.AdminConsts;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.springframework.scheduling.TaskScheduler;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.Duration;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineSessionManager extends DefaultWebSessionManager {

    private TaskScheduler taskScheduler;
    private FrameworkProperties properties;

    public OnlineSessionManager(TaskScheduler taskScheduler
            , FrameworkProperties frameworkProperties) {
        this.taskScheduler = taskScheduler;
        this.properties = frameworkProperties;
    }

    /**
     * session验证的调度器交由spring的任务调度
     * @see TaskScheduler
     * @return
     */
    protected SessionValidationScheduler createSessionValidationScheduler() {
        if(taskScheduler == null) {
            return super.createSessionValidationScheduler();
        } else { // 使用spring 任务调度
            return new SessionValidationScheduler() {
                private boolean enabled = false;

                @Override
                public void enableSessionValidation() {
                    this.enabled = true;

                    OnlineSessionManager sessionManager = OnlineSessionManager.this;
                    if (sessionManager.getSessionValidationInterval() > 0l) {
                        sessionManager.taskScheduler.scheduleAtFixedRate(() -> {
                            sessionManager.validateSessions();
                        }, Duration.ofMillis(sessionManager.getSessionValidationInterval()));
                    }
                }

                @Override
                public void disableSessionValidation() {
                    /*do nothing*/
                }

                @Override
                public boolean isEnabled() {
                    return enabled;
                }
            };
        }
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

        // 从cookie中获取
        Serializable sessionId = super.getSessionId(request, response);
        createWebDelegatingSubject(request, response);
        if(null != sessionId) return sessionId;

        // cookie中没有， 如果是原生App 尝试从header获取access_token作为sessionId
        Browser browser = userAgent.getBrowser();
        if(browser != null) {
            BrowserType browserType = browser.getBrowserType();

            if(browserType == BrowserType.APP) { // 原生App
                FrameworkProperties.Auth auth = properties.getAuth();

                // 请求头包含access_token, 说明是app请求
                sessionId = servletRequest.getHeader(auth.getAppTokenKey());
            }
        }


        return sessionId;
    }

    private void createWebDelegatingSubject(ServletRequest request, ServletResponse response) {
        WebDelegatingSubject subject = new WebDelegatingSubject(null
                , false, null, null, request, response, SecurityUtils.getSecurityManager());

        ThreadContext.bind(subject);
    }
}
