package com.iteaj.framework.security.shiro;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spi.auth.SecurityException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import static com.iteaj.framework.consts.CoreConst.EXEC_EXCEPTION_STATUS;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroFrameworkAuthRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof ShiroTokenDelegate;
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
        Object principal = token.getPrincipal();
        if(null == principal) {
            throw new AuthenticationException("用户不存在");
        }

        ShiroTokenDelegate delegate = (ShiroTokenDelegate) token;
        return new SimpleAuthenticationInfo(delegate.getAuthToken()
                , token.getCredentials(), this.getName());
    }

    /**
     * 超级管理员包含所有的权限
     * @param principals
     * @param permission
     * @return
     */
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        if(ShiroUtil.isSuper()) {
            return true;
        }

        final boolean permitted = super.isPermitted(principals, permission);
        if(!permitted) {
            ShiroUtil.setRequestAttr(EXEC_EXCEPTION_STATUS, "无权限");
            throw new SecurityException("无权限");
        }

        return true;
    }

    @Override
    public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
        return ShiroUtil.isSuper() ||
            super.hasRole(principal, roleIdentifier);
    }
}
