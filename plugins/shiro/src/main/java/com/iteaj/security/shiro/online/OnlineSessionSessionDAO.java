package com.iteaj.security.shiro.online;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.OnlineSession;
import com.iteaj.framework.spi.admin.auth.OnlineService;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.WebSession;

import java.io.Serializable;
import java.util.Date;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineSessionSessionDAO extends CachingSessionDAO implements DisposableBean {

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
     * 1. 如果缓存里面找不到session, 那从数据库存储等地方读取session
     * 2. 如果此session已经登录并且为过期则自动登录 {@link OnlineSubjectDao#save(Subject)}
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

        // 验证session是否过期或者已经停止
        try {
            session.validate();
        } catch (InvalidSessionException e) {
            return session;
        }

        return session;
    }

    @Override
    public void destroy() throws Exception {

    }
}
