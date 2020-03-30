package com.iteaj.admin.service;

import com.google.code.kaptcha.Constants;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import com.iteaj.izone.spi.AuthorizingService;
import com.iteaj.izone.web.shiro.ShiroAuthToken;
import com.iteaj.util.module.mvc.orm.Entity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Objects;

public class UserPasswordRealm extends AuthorizingRealm {

    @Autowired
    private FrameworkProperties frameworkProperties;
    private AuthorizingService authorizingService;

    public UserPasswordRealm(AuthorizingService authorizingService) {
        this(null, null, authorizingService);
    }

    public UserPasswordRealm(CacheManager cacheManager, CredentialsMatcher matcher, AuthorizingService authorizingService) {
        super(cacheManager, matcher);
        this.authorizingService = authorizingService;
        Assert.notNull(authorizingService, "为创建授权服务："+AuthorizingService.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if(!(token instanceof UsernamePasswordToken))
            throw new UnsupportedTokenException("暂不支持使用其他token登录");

        Session session = SecurityUtils.getSubject().getSession();
        ShiroAuthToken authToken = (ShiroAuthToken) token;

        if(frameworkProperties.getAuth().isCaptchaEnabled()) {
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
        clearAuthenticationInfo(session, token);

        // 触发登录认证事件
        return new SimpleAuthenticationInfo(admin, password, getName());
    }

    private void clearAuthenticationInfo(Session session, AuthenticationToken token) {
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

    @Override
    public void onLogout(PrincipalCollection principals) {
        super.onLogout(principals);
    }
}
