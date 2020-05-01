package com.iteaj.admin.service;

import com.google.code.kaptcha.Constants;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.framework.utils.ShiroUtils;
import com.iteaj.izone.spi.AuthorizingService;
import com.iteaj.izone.web.shiro.ShiroAuthToken;
import com.iteaj.util.module.mvc.orm.Entity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AdminUserPasswordRealm extends AuthorizingRealm {

    private final FrameworkProperties properties;
    private final AuthorizingService authorizingService;

    public AdminUserPasswordRealm(@Qualifier("securityManager") RealmSecurityManager securityManager
            , FrameworkProperties properties, AuthorizingService authorizingService) {
        securityManager.setRealm(this);
        this.properties = properties;
        this.authorizingService = authorizingService;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if(!(token instanceof UsernamePasswordToken))
            throw new UnsupportedTokenException("不支持的token");

        Session session = SecurityUtils.getSubject().getSession();
        ShiroAuthToken authToken = (ShiroAuthToken) token;

        if(properties.getAuth().isCaptchaEnabled()) {
            String kaptcha = (String)session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if(!Objects.equals(kaptcha, authToken.getCaptcha())) {
                throw new AuthenticationException("验证码错误");
            }
        }

        // 验证账号是否存在
        String username = authToken.getUsername();
        Entity admin = authorizingService.getAdmin(authToken);
        if(null == admin) throw new UnknownAccountException("账号不存在：" + username);

        // 校验密码
        boolean authentication = authorizingService.validCredentials(admin, authToken);
        if(!authentication) throw new CredentialsException("账号密码不匹配");

        String password = new String(((UsernamePasswordToken) token).getPassword());
        return new SimpleAuthenticationInfo(admin, password, getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        BaseEntity admin = (BaseEntity)principals.getPrimaryPrincipal();

        // 创建权限对象, 并设置角色和权限信息
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(authorizingService.getRoles(admin));
        authorizationInfo.addStringPermissions(authorizingService.getPermissions(admin));

        return authorizationInfo;
    }

    /**
     * 超级管理员包含所有的权限
     * @param principals
     * @param permission
     * @return
     */
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        BaseEntity admin = (BaseEntity)principals.getPrimaryPrincipal();
        return ShiroUtils.isSuper(admin.getId()) || super.isPermitted(principals, permission);
    }

    @Override
    public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
        BaseEntity admin = (BaseEntity)principal.getPrimaryPrincipal();
        return ShiroUtils.isSuper(admin.getId()) || super.hasRole(principal, roleIdentifier);
    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        super.onLogout(principals);
    }
}
