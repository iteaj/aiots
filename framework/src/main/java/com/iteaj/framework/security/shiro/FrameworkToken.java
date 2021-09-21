package com.iteaj.framework.security.shiro;

import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.Entity;
import org.apache.shiro.authc.AuthenticationToken;

import java.util.Collection;
import java.util.Collections;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class FrameworkToken implements AuthToken, AuthenticationToken {

    private Entity principal;
    private Object credentials;

    private Collection<String> roles = Collections.emptyList();
    private Collection<String> permissions = Collections.emptyList();

    public FrameworkToken(Entity principal, Object credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    public FrameworkToken(Entity principal, Object credentials
            , Collection<String> roles, Collection<String> permissions) {
        this.principal = principal;
        this.credentials = credentials;
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Collection<String> getRoles() {
        return this.roles;
    }

    @Override
    public Collection<String> getPermissions() {
        return this.permissions;
    }
}
