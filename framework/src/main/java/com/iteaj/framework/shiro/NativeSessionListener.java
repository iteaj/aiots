package com.iteaj.framework.shiro;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.admin.event.OnlineEvent;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.framework.spi.event.EventUtils;
import com.iteaj.framework.web.WebUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;

import javax.servlet.http.HttpServletRequest;

/**
 * create time: 2020/6/21
 *
 * @author iteaj
 * @since 1.0
 */
public class NativeSessionListener implements SessionListener {

    @Override
    public void onStart(Session session) {
        Subject subject = SecurityUtils.getSubject();

        HttpServletRequest servletRequest = (ShiroHttpServletRequest)
                ((WebDelegatingSubject) subject).getServletRequest();;

        UserAgent userAgent = (UserAgent) servletRequest.getAttribute(CoreConst.WEB_USER_AGENT);

        // 发布session创建事件
        EventUtils.publish(new OnlineEvent(session.getId(), userAgent)
                .setAccessTime(session.getStartTimestamp()).setTimeout(session.getTimeout())
                .setType(OnlineType.Guest).setAccessIp(WebUtils.getIpAddress(servletRequest)));
    }

    @Override
    public void onStop(Session session) {
        // 发布session 停止事件
        EventUtils.publish(new OnlineEvent(session.getId(), OnlineType.Off)
                .setAccessTime(session.getLastAccessTime()));
    }

    /**
     * todo {@link SecurityUtils#getSecurityManager()} 异常, 读取不到
     * @param session
     */
    @Override
    public void onExpiration(Session session) {
        // 发布session 过期事件
        EventUtils.publish(new OnlineEvent(session.getId(), OnlineType.Off)
                .setAccessTime(session.getLastAccessTime()));
    }
}
