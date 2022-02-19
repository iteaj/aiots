package com.iteaj.framework.security.shiro;

import com.iteaj.framework.Entity;
import com.iteaj.framework.spi.admin.event.OnlineEvent;
import com.iteaj.framework.spi.admin.event.OnlineStatus;
import com.iteaj.framework.spi.event.EventUtils;
import com.iteaj.framework.web.WebUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.subject.PrincipalCollection;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * create time: 2020/6/21
 *  在线会话监听
 * @author iteaj
 * @since 1.0
 */
public class ShiroOnlineListener implements SessionListener, AuthenticationListener {

    /**
     * 新开会话
     * @param session
     */
    @Override
    public void onStart(Session session) { }

    /**
     * 会话停止
     * @param session
     */
    @Override
    public void onStop(Session session) {
        // 发布session 停止事件
        EventUtils.publish(new OnlineEvent(session.getId(), OnlineStatus.Offline)
                .setAccessTime(session.getLastAccessTime()));
    }

    /**
     * 会话过期
     * @param session
     */
    @Override
    public void onExpiration(Session session) {
        // 发布session 过期事件
        EventUtils.publish(new OnlineEvent(session.getId(), OnlineStatus.Offline)
                .setAccessTime(session.getLastAccessTime()));
    }

    @Override
    public void onSuccess(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        try {
            Entity admin;
            if(authenticationToken.getPrincipal() instanceof Entity){
                admin = (Entity) authenticationToken.getPrincipal();
            } else {
                admin = null;
            }

            final UserAgent agent = ShiroUtil.getAgent();
            final String ipAddress = WebUtils.getIpAddress(ShiroUtil.getRequest());
            final HttpSession session = ShiroUtil.getRequest().getSession();
            final String sessionId = session.getId();

            EventUtils.publish(new OnlineEvent(sessionId, OnlineStatus.Online)
                    .setAdmin(admin).setUserAgent(agent).setAccessIp(ipAddress)
                    .setAccessTime(new Date(session.getLastAccessedTime()))
                    .setExpireTime(session.getMaxInactiveInterval()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(AuthenticationToken authenticationToken, AuthenticationException e) { }

    @Override
    public void onLogout(PrincipalCollection principalCollection) {
        final String sessionId = ShiroUtil.getRequest().getSession().getId();
        EventUtils.publish(new OnlineEvent(sessionId, OnlineStatus.Offline).setAccessTime(new Date()));
    }
}
