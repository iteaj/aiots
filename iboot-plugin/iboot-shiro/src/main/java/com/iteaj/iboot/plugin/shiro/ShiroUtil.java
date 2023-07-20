package com.iteaj.iboot.plugin.shiro;

import com.iteaj.framework.Entity;
import com.iteaj.framework.consts.CoreConst;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.NativeSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.util.ThreadContext;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class ShiroUtil {

    public static Serializable getId() {
        final Entity user = getUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 返回当前登录的用户
     * @return
     */
    public static <T extends Entity> T getUser() {
        try {
            final Object principal = SecurityUtils.getSubject().getPrincipal();
            if(principal instanceof Entity) {
                return ((T) principal);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 是否已经认证登录
     * @return
     */
    public static boolean isLogin() {
        return getUser() != null;
    }

    /**
     * 后台管理端：当前用户是否超级管理员
     * @return
     */
    public static boolean isSuper() {
        if(getUser().getId() instanceof Number) {
            return isSuper(((Number) getUser().getId()).longValue());
        }

        return false;
    }

    /**
     * 后台管理端：验证用户是不是超级管理员
     * @param id
     * @return
     */
    public static boolean isSuper(Long id) {
        return id == 1l;
    }

    /**
     * 剔除用户下线
     * @param session
     */
    public static void offline(Object session) throws UnknownSessionException {
        final SecurityManager securityManager = SecurityUtils.getSecurityManager();
        if(securityManager instanceof SessionsSecurityManager) {
            final SessionManager sessionManager = ((SessionsSecurityManager) securityManager).getSessionManager();
            if(sessionManager instanceof NativeSessionManager) {
                final DefaultSessionKey sessionKey = new DefaultSessionKey((Serializable) session);
                ((NativeSessionManager) sessionManager).stop(sessionKey);
            }
        } else {
            throw new IllegalStateException("不支持此操作");
        }
    }

    public static Session getSession(String sessionId) {
        final SecurityManager securityManager = SecurityUtils.getSecurityManager();
        if(securityManager instanceof SessionsSecurityManager) {
            final SessionManager sessionManager = ((SessionsSecurityManager) securityManager).getSessionManager();
            if(sessionManager instanceof NativeSessionManager) {
                try {
                    return sessionManager.getSession(new DefaultSessionKey(sessionId));
                } catch (UnknownSessionException e) {
                    return null;
                }
            } else {
                throw new IllegalStateException("不支持此操作");
            }
        } else {
            throw new IllegalStateException("不支持此操作");
        }
    }

    public static UserAgent getAgent() {
        return (UserAgent) getRequest().getAttribute(CoreConst.WEB_USER_AGENT);
    }

    /**
     * 获取当前请求
     * @return
     */
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) ThreadContext.get(CoreConst.HTTP_SERVLET_REQUEST);
    }

    public static <T> T getRequestAttr(String key) {
        return (T) getRequest().getAttribute(key);
    }

    public static void setRequestAttr(String key, Object value) {
        getRequest().setAttribute(key, value);
    }
}
