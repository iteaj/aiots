package com.iteaj.security.shiro.app;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * create time: 2021/3/23
 *
 * @author iteaj
 * @since 1.0
 */
public class AppAuthenticationToken implements AuthenticationToken {

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
