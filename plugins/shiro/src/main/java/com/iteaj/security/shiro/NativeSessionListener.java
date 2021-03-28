package com.iteaj.security.shiro;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.admin.event.OnlineEvent;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.framework.spi.event.EventPublisher;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * create time: 2020/6/21
 *
 * @author iteaj
 * @since 1.0
 */
public class NativeSessionListener implements SessionListener {

    @Autowired
    private EventPublisher publisher;

    @Override
    public void onStart(Session session) {
        Subject subject = SecurityUtils.getSubject();

        ShiroHttpServletRequest servletRequest = (ShiroHttpServletRequest) WebUtils.getRequest(subject);
        UserAgent userAgent = (UserAgent) servletRequest.getAttribute(CoreConst.WEB_USER_AGENT);

        // 发布session创建事件
        publisher.publish(new OnlineEvent(session.getId(), userAgent)
                .setAccessTime(session.getStartTimestamp()).setTimeout(session.getTimeout())
                .setType(OnlineType.Guest).setAccessIp(getIpAddress(servletRequest)));
    }

    @Override
    public void onStop(Session session) {
        // 发布session 停止事件
        publisher.publish(new OnlineEvent(session.getId(), OnlineType.Off)
                .setAccessTime(session.getLastAccessTime()));
    }

    /**
     * todo {@link SecurityUtils#getSecurityManager()} 异常, 读取不到
     * @param session
     */
    @Override
    public void onExpiration(Session session) {
        // 发布session 过期事件
        publisher.publish(new OnlineEvent(session.getId(), OnlineType.Off)
                .setAccessTime(session.getLastAccessTime()));
    }

    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
