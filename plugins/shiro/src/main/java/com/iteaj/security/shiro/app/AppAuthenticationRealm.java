package com.iteaj.security.shiro.app;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 * create time: 2021/3/23
 *
 * @author iteaj
 * @since 1.0
 */
public class AppAuthenticationRealm extends AuthenticatingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return AppAuthenticationToken.class.isAssignableFrom(token.getClass());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        AppAuthenticationToken appToken = (AppAuthenticationToken) token;


        SimplePrincipalCollection principal = new SimplePrincipalCollection(appToken.getPrincipal(), getName());
        return new SimpleAuthenticationInfo(principal, appToken.getCredentials());
    }

}
