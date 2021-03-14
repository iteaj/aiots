package com.iteaj.security.shiro.online;

import com.iteaj.framework.spi.OnlineSession;
import com.iteaj.framework.spi.admin.auth.OnlineService;
import com.iteaj.security.shiro.AutoLoginToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineSessionSessionDAO extends EnterpriseCacheSessionDAO implements DisposableBean {

    @Autowired
    private OnlineService onlineService;

    @Override
    public Serializable create(Session session) {
        return super.create(session);
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session cachedSession = getCachedSession(sessionId);
        if(cachedSession != null) return cachedSession;

        OnlineSession onlineSession = onlineService.getOnlineSession(sessionId);
        if(onlineSession == null) return null;

        SimpleSession session = new SimpleSession();
        session.setId(onlineSession.getSessionId());
        session.setTimeout(onlineSession.getTimeout());
        session.setStartTimestamp(onlineSession.getStartTimestamp());
        session.setLastAccessTime(onlineSession.getLastAccessTime());

        if(session instanceof ValidatingSession) {
            try {
                session.validate(); // 首先先验证session

                cache(session, sessionId); // 写入session到缓存

                // 必须是之前已经登录过的会话, 才能自动登录
                if(StringUtils.hasText(onlineSession.getAccount())) {

                    /**
                     * 用于自动登录
                     * @see com.iteaj.security.shiro.AdminUserPasswordRealm#assertCredentialsMatch 去除自动登录的密码认证
                     * @see com.iteaj.security.shiro.AdminUserPasswordRealm#autoLoginAuthenticationInfo(AutoLoginToken)
                     */
                    AutoLoginToken autoLoginToken = new AutoLoginToken(onlineSession.getAccount(), sessionId);

                    /**
                     * @see OnlineSessionManager#createWebDelegatingSubject ori再此处创建
                     */
                    WebDelegatingSubject ori = (WebDelegatingSubject)SecurityUtils.getSubject();

                    // 此处必须重新创建一个WebDelegatingSubject对象, 并且指定传入当前登录的session
                    WebDelegatingSubject subject = new WebDelegatingSubject(ori.getPrincipals(), ori.isAuthenticated(), ori.getHost()
                            , session, ori.getServletRequest(), ori.getServletResponse(), ori.getSecurityManager());
                    SecurityUtils.getSecurityManager().login(subject, autoLoginToken); // 自动登录

                    // 复制已经登录的session信息
                    copyLoginSessionAttrs(subject.getSession(), session);
                }
            } catch (InvalidSessionException e) {
                e.printStackTrace();
            }
        }

        return session;
    }

    /**
     * 复制登录的属性
     * @see SubjectContext#isAuthenticated() 验证是否登录
     * @see SubjectContext#resolvePrincipals() 获取登录的用户
     * @param subjectSession
     * @param session
     */
    private void copyLoginSessionAttrs(Session subjectSession, SimpleSession session) {
        Collection<Object> attributeKeys = subjectSession.getAttributeKeys();

        if(CollectionUtils.isEmpty(attributeKeys)) return;

        attributeKeys.forEach(key->{
            session.setAttribute(key, subjectSession.getAttribute(key));
        });
    }

    @Override
    public void destroy() throws Exception {

    }
}
