package com.iteaj.msn.core.auth;

import com.google.code.kaptcha.Constants;
import com.iteaj.framework.security.shiro.ShiroUtil;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spi.auth.SecurityException;
import com.iteaj.msn.core.entity.Admin;
import com.iteaj.msn.core.service.IAdminService;
import com.iteaj.util.DigestUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroAdminAuthRealm extends AuthorizingRealm {

    @Autowired
    private IAdminService adminService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && AdminAuthToken.class.isAssignableFrom(token.getClass());
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
        AdminAuthToken adminAuthToken = (AdminAuthToken) token;

        String userName = adminAuthToken.getUsername();
        String password = new String(adminAuthToken.getPassword());
        String captcha = adminAuthToken.getCaptcha();

        final HttpServletRequest request = ShiroUtil.getRequest();
        final HttpSession session = request.getSession();
        final Object attribute = session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        final Long createTime = (Long) session.getAttribute(Constants.KAPTCHA_SESSION_DATE);

        if(!Objects.equals(attribute, captcha) || createTime == null) {
            throw new SecurityException("验证码错误");
        } else {
            session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
            session.removeAttribute(Constants.KAPTCHA_SESSION_DATE);
        }

        Admin admin = adminService.getByAccount(userName);
        if(admin == null) {
            throw new AccountException("账户不存在");
        }

        // 密码校验
        String passwordDigest = admin.getPassword().toUpperCase();
        String md5Hex = DigestUtils.md5Hex(password.getBytes()).toUpperCase();

        if(!Objects.equals(passwordDigest, md5Hex)) {
            throw new CredentialsException("密码校验失败");
        }

        return new SimpleAuthenticationInfo(admin, token.getCredentials(), this.getName());
    }

    /**
     * 超级管理员包含所有的权限
     * @param principals
     * @param permission
     * @return
     */
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {

        return ShiroUtil.isSuper() ||
                super.isPermitted(principals, permission);
    }

    @Override
    public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {

        return ShiroUtil.isSuper() ||
            super.hasRole(principal, roleIdentifier);
    }
}
