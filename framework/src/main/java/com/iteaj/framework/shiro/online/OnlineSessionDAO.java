package com.iteaj.framework.shiro.online;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.OnlineSession;
import com.iteaj.framework.spi.admin.auth.OnlineService;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineSessionDAO extends CachingSessionDAO {

    @Autowired
    private OnlineService onlineService;
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        return sessionId;
    }

    @Override
    protected void doUpdate(Session session) {
        /*只更新缓存不更新数据库*/
    }

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

        session.setAttribute(CoreConst.ONLINE_ACCOUNT_ID, onlineSession.getAccount());

        return session;
    }

}
