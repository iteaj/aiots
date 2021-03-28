package com.iteaj.core.auth;

import com.iteaj.core.entity.Admin;
import com.iteaj.framework.spi.auth.AuthToken;

import java.util.Collection;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class AdminAuthToken implements AuthToken {

    private Admin admin;
    private Collection<String> roles;
    private Collection<String> permissions;

    public AdminAuthToken(Admin admin, Collection<String> roles, Collection<String> permissions) {
        this.admin = admin;
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public Object getPrincipal() {
        return this.admin;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Collection<String> roles() {
        return this.roles;
    }

    @Override
    public Collection<String> permissions() {
        return this.permissions;
    }
}
