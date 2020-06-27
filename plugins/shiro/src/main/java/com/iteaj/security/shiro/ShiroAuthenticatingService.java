package com.iteaj.security.shiro;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.framework.spi.admin.auth.AuthenticatingService;
import com.iteaj.framework.spi.admin.event.OnlineEvent;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.framework.spi.event.EventPublisher;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroAuthenticatingService implements AuthenticatingService<ShiroAuthToken> {

    private AuthzUrl authzUrl;
    private EventPublisher eventPublisher;

    @Override
    public void logout(Serializable sessionId) {
        if(sessionId == null) throw new IllegalArgumentException("sessionId必传");

        SecurityManager securityManager = SecurityUtils.getSecurityManager();

        DefaultSessionKey defaultSessionKey = new DefaultSessionKey();
        defaultSessionKey.setSessionId(sessionId);

        try {
            Session session = securityManager.getSession(defaultSessionKey);

            // 停止session
            session.stop();

            // 发布离线事件
            eventPublisher.publish(new OnlineEvent(sessionId,
                    OnlineType.Off).setAccessTime(session.getLastAccessTime()));
        } catch (SessionException e) {

            // 发布离线事件
            eventPublisher.publish(new OnlineEvent(sessionId, OnlineType.Off));
        }
    }

    @Override
    public boolean equalToCurrent(Serializable sessionId) {
        Session session = SecurityUtils.getSubject().getSession();
        return session.getId().equals(sessionId);
    }

    @Override
    public BaseEntity getPrincipal() {
        return (BaseEntity) SecurityUtils.getSubject().getPrincipal();
    }

    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    @Autowired
    public ShiroAuthenticatingService setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        return this;
    }

    @Override
    public AuthzUrl getAuthzUrl() {
        return authzUrl;
    }

    @Autowired
    public ShiroAuthenticatingService setAuthzUrl(AuthzUrl authzUrl) {
        this.authzUrl = authzUrl;
        return this;
    }

    protected Logger getLogger() {
        return com.iteaj.framework.spi.admin.SecurityUtils.LOGGER;
    }
}
