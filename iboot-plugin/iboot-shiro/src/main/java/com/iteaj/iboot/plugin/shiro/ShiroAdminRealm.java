package com.iteaj.iboot.plugin.shiro;

import com.google.code.kaptcha.Constants;
import com.iteaj.framework.Entity;
import com.iteaj.framework.security.AuthenticationService;
import com.iteaj.framework.security.AuthorizationService;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.framework.spi.admin.auth.AuthenticationUser;
import com.iteaj.util.DigestUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Objects;

/**
 * create time: 2021/3/27
 *  shiro 后台管理端认证和授权的实现
 * @author iteaj
 * @since 1.0
 */
public class ShiroAdminRealm extends AuthorizingRealm {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && ShiroSecurityToken.class.isAssignableFrom(token.getClass());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Entity admin = (Entity) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        Collection<String> permissions = this.authorizationService.getPermissions(admin.getId());
        authorizationInfo.addStringPermissions(permissions);

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        ShiroSecurityToken shiroAdminToken = (ShiroSecurityToken) token;

        String userName = shiroAdminToken.getAccount();
        String password = shiroAdminToken.getPassword();

        final HttpServletRequest request = ShiroUtil.getRequest();
        final HttpSession session = request.getSession();

        try {
            /**
             * 获取校验码
             * @see com.iteaj.framework.autoconfigure.CaptchaAutoConfiguration#captcha(HttpSession, HttpServletResponse) 获取校验码接口
             */
            String captcha = shiroAdminToken.getCaptcha();
            final Object attribute = session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
            final Long createTime = (Long) session.getAttribute(Constants.KAPTCHA_SESSION_DATE);

            if(!Objects.equals(attribute, captcha) || createTime == null) {
                throw new AuthenticationException("验证码错误");
            }

            AuthenticationUser admin = authenticationService.getByAccount(userName);
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
        } finally {
            session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
            session.removeAttribute(Constants.KAPTCHA_SESSION_DATE);
        }
    }

    /**
     * 超级管理员包含所有的权限
     * @param principals
     * @param permission
     * @return
     */
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        return SecurityUtil.isSuper() ||
                super.isPermitted(principals, permission);
    }

    /**
     * 超级管理员拥有所有的角色
     * @param principal
     * @param roleIdentifier
     * @return
     */
    @Override
    public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
        return SecurityUtil.isSuper() ||
            super.hasRole(principal, roleIdentifier);
    }
}
