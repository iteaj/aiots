package com.iteaj.framework.security.shiro.online;

import com.iteaj.framework.spi.OnlineSession;
import com.iteaj.framework.spi.admin.auth.OnlineService;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineSessionDAO extends EnterpriseCacheSessionDAO implements InitializingBean {

    @Autowired
    private OnlineService onlineService;

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
    public void afterPropertiesSet() throws Exception {
//        setActiveSessionsCacheName(cacheProperties.getName());
    }
}
