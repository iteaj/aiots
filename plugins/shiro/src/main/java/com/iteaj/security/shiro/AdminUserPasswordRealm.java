package com.iteaj.security.shiro;

import com.google.code.kaptcha.Constants;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.admin.AdminConsts;
import com.iteaj.framework.spi.admin.LoggerType;
import com.iteaj.framework.spi.admin.auth.UserAccountService;
import com.iteaj.framework.spi.admin.event.OnlineEvent;
import com.iteaj.framework.spi.admin.event.OnlineType;
import com.iteaj.framework.spi.event.EventPublisher;
import com.iteaj.framework.spring.boot.config.FrameworkWebProperties;
import com.iteaj.framework.spi.admin.SecurityUtils;
import com.iteaj.framework.spi.admin.auth.AuthorizingService;
import com.iteaj.util.module.mvc.orm.Entity;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class AdminUserPasswordRealm extends AuthorizingRealm {

    private final EventPublisher eventPublisher;
    private final FrameworkWebProperties properties;
    private final AuthorizingService authorizingService;
    private final UserAccountService userAccountService;

    public AdminUserPasswordRealm(RealmSecurityManager securityManager
            , EventPublisher eventPublisher, FrameworkWebProperties properties
            , AuthorizingService authorizingService, UserAccountService userAccountService) {
        this.properties = properties;
        securityManager.setRealm(this);
        this.eventPublisher = eventPublisher;
        this.authorizingService = authorizingService;
        this.userAccountService = userAccountService;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 会话未过期的自动登录功能
        if(token instanceof AutoLoginToken) {
            return autoLoginAuthenticationInfo((AutoLoginToken) token);
        }

        long start = 0;
        Object source = token, msg = "";
        WebSubject subject = (WebSubject)org.apache.shiro.SecurityUtils.getSubject();
        try {
            start = System.currentTimeMillis();
            if(!(token instanceof UsernamePasswordToken))
                throw new UnsupportedTokenException("不支持的token");

            Session session = subject.getSession();
            ShiroAuthToken authToken = (ShiroAuthToken) token;

            if(properties.getAuth().isCaptchaEnabled()) {
                String kaptcha = (String)session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
                if(!Objects.equals(kaptcha, authToken.getCaptcha())) {
                    throw new AuthenticationException("验证码错误");
                }
            }

            // 验证账号是否存在
            String username = authToken.getUsername();
            Entity admin = userAccountService.getAdmin(authToken);
            source = admin;
            if(null == admin) throw new UnknownAccountException("账号不存在：" + username);

            // 校验密码
            boolean authentication = userAccountService.validCredentials(admin, authToken);
            if(!authentication) throw new CredentialsException("账号密码不匹配");

            // 发布登录事件
            this.publishOnlineEvent(admin);

            // 如果是app端登录, 写access_token到header
            writeAccessTokenToHeader(subject);

            String password = new String(((UsernamePasswordToken) token).getPassword());
            return new SimpleAuthenticationInfo(admin, password, getName());
        } catch (InvalidSessionException e) {
            msg = e.getMessage();
            throw e;
        } catch (AuthenticationException e) {
            msg = e.getMessage();
            throw e;
        } finally {
            long time = System.currentTimeMillis() - start;

            ShiroAuthorEvent event = new ShiroAuthorEvent(source
                    , (ShiroHttpServletRequest) subject.getServletRequest()
                    , LoggerType.Login).setMethod(null).setErrMsg((String) msg)
                    .setOperaTimeMillis(time).setArgs(new Object[]{token});
            this.eventPublisher.publish(event);
        }

    }

    private AuthenticationInfo autoLoginAuthenticationInfo(AutoLoginToken token) {
        Entity admin = userAccountService.getAdmin(token);
        if(null == admin) throw new UnknownAccountException("账号不存在：" + token.getUsername());

        // 发送登录事件
        this.eventPublisher.publish(new OnlineEvent(token
                .getSessionId(), OnlineType.On).setAdmin(admin));

        return new SimpleAuthenticationInfo(admin, null, getName());
    }

    /**
     * 如果请求端是原生app, 则写入请求数据到access_token
     * @param subject
     */
    private void writeAccessTokenToHeader(WebSubject subject) {
        ServletRequest request = WebUtils.getRequest(subject);
        HttpServletResponse response = (HttpServletResponse)WebUtils.getResponse(subject);

        UserAgent userAgent = (UserAgent) request.getAttribute(AdminConsts.USER_AGENT);
        Browser browser = userAgent.getBrowser();
        if(null != browser) {
            BrowserType browserType = browser.getBrowserType();
            if(browserType == BrowserType.APP) {
                FrameworkWebProperties.Auth auth = properties.getAuth();

                // 写入access_token到header
                response.addHeader(auth.getAppTokenKey()
                        , (String) subject.getSession().getId());
            }
        }
    }

    private void publishOnlineEvent(Entity admin) {
        Session session = org.apache.shiro.SecurityUtils.getSubject().getSession();

        this.eventPublisher.publish(new OnlineEvent(session.getId()
                , OnlineType.On).setAdmin(admin));
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
     * 如果是自动登录无需校验密码
     * @param token
     * @param info
     * @throws AuthenticationException
     */
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        if(token instanceof AutoLoginToken) {

        } else {
            super.assertCredentialsMatch(token, info);
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
        BaseEntity admin = (BaseEntity)principals.getPrimaryPrincipal();
        return SecurityUtils.isSuper(admin.getId())
                || super.isPermitted(principals, permission);
    }

    @Override
    public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
        BaseEntity admin = (BaseEntity)principal.getPrimaryPrincipal();
        return SecurityUtils.isSuper(admin.getId())
                || super.hasRole(principal, roleIdentifier);
    }
}
