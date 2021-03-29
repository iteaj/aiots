package com.iteaj.security.shiro;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.admin.auth.FrameworkAuthService;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.util.module.mvc.orm.Entity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import java.io.Serializable;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroAuthService implements FrameworkAuthService<ShiroAuthToken> {

    private WebAuthHandler authHandler;

    public ShiroAuthService(WebAuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @Override
    public boolean isCurrent(Serializable sessionId) {
        Session session = SecurityUtils.getSubject().getSession();
        return session.getId().equals(sessionId);
    }

    @Override
    public Entity getUser() {
        final Object principal = SecurityUtils.getSubject().getPrincipal();
        if(principal instanceof AuthToken) {
            return (Entity)((AuthToken) principal).getPrincipal();
        }

        return (Entity) principal;
    }

    @Override
    public WebAuthAction getAction() {
        final Session session = SecurityUtils.getSubject().getSession(false);
        if(session != null) {
            final Object actionName = session.getAttribute(CoreConst.AUTH_ACTION_NAME_CURRENT);

            return authHandler.getAction((String) actionName);
        }

        return null;
    }
}
