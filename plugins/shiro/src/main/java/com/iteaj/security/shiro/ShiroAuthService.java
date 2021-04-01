package com.iteaj.security.shiro;

import com.iteaj.framework.spi.admin.auth.FrameworkAuthService;
import com.iteaj.framework.spi.auth.AuthToken;
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

}
