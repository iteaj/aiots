package com.iteaj.framework.shiro;

import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.framework.spi.auth.AuthToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroFrameworkAuthRealm extends AuthorizingRealm {

    public ShiroFrameworkAuthRealm() { }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && ShiroAuthTokenAdapter
                .class.isAssignableFrom(token.getClass());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        AuthToken authToken = (AuthToken) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        authorizationInfo.addRoles(authToken.getRoles());
        authorizationInfo.addStringPermissions(authToken.getPermissions());

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        ShiroAuthTokenAdapter authTokenAdapter = (ShiroAuthTokenAdapter) token;

        if(authTokenAdapter.getAuthToken() == null) {
            throw new UnsupportedTokenException("认证失败[null]");
        }

        Object principal = token.getPrincipal();
        if(null == principal) {
            throw new AuthenticationException("用户不存在");
        }

        return new SimpleAuthenticationInfo(authTokenAdapter
                .getAuthToken(), null, this.getName());
    }

    /**
     * 超级管理员包含所有的权限
     * @param principals
     * @param permission
     * @return
     */
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        return SecurityUtils.isSuper() ||
            super.isPermitted(principals, permission);
    }

    @Override
    public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
        return SecurityUtils.isSuper() ||
            super.hasRole(principal, roleIdentifier);
    }
}
