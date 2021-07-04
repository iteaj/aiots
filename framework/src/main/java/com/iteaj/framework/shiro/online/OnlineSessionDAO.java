package com.iteaj.framework.shiro.online;

import com.iteaj.framework.autoconfigure.SessionCacheProperties;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.OnlineSession;
import com.iteaj.framework.spi.admin.auth.OnlineService;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.utils.ShiroUtil;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineSessionDAO extends EnterpriseCacheSessionDAO implements InitializingBean {

    @Autowired
    private OnlineService onlineService;
    @Autowired
    private SessionCacheProperties cacheProperties;

    @Override
    protected void doDelete(Session session) {
        onlineService.removeBySessionId(session.getId());
    }

    /**
     * @param sessionId
     * @return
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {

        OnlineSession onlineSession = onlineService.getSession(sessionId);

        if(onlineSession == null) {
            return null;
        }

        // 声明此session是一个在线session
        SimpleSession session = new SimpleSession();
        session.setId(sessionId);
        session.setTimeout(onlineSession.getTimeout());
        session.setStartTimestamp(onlineSession.getStartTimestamp());
        session.setLastAccessTime(onlineSession.getLastAccessTime());

        // 缓存
        cache(session, sessionId);
        return session;
    }

    @Override
    protected Session getCachedSession(Serializable sessionId) {
        Session cached = null;
        if (sessionId != null) {
            Cache<Serializable, Session> cache = getSessionsCache();
            if (cache != null) {
                cached = getCachedSession(sessionId, cache);
            }
        }
        return cached;
    }

    private Cache<Serializable, Session> getSessionsCache() {
        final WebAuthAction action = ShiroUtil.getRequestAttr(CoreConst.HANDLE_AUTH_ACTION);
        if(action != null) {
            return getCacheManager().getCache(action.getName());
        }

        return null;
    }

    @Override
    protected void cache(Session session, Serializable sessionId) {
        if (session == null || sessionId == null) {
            return;
        }

        Cache<Serializable, Session> cache = getSessionsCache();
        if (cache == null) {
            return;
        }

        cache(session, sessionId, cache);
    }

    @Override
    protected void uncache(Session session) {
        if (session == null) {
            return;
        }
        Serializable id = session.getId();
        if (id == null) {
            return;
        }

        Cache<Serializable, Session> cache = getSessionsCache();
        if (cache != null) {
            cache.remove(id);
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Cache<Serializable, Session> cache = getSessionsCache();
        if (cache != null) {
            return cache.values();
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public String getActiveSessionsCacheName() {
        throw new UnsupportedOperationException("不支持此操作");
    }

    @Override
    public void setActiveSessionsCacheName(String activeSessionsCacheName) {
        throw new UnsupportedOperationException("不支持此操作");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        setActiveSessionsCacheName(cacheProperties.getName());
    }
}
