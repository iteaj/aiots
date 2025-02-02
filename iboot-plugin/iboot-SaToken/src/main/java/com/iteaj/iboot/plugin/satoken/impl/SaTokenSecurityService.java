package com.iteaj.iboot.plugin.satoken.impl;

import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.iteaj.framework.Entity;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.captcha.Captcha;
import com.iteaj.framework.captcha.CaptchaService;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.security.SecurityException;
import com.iteaj.framework.security.*;
import com.iteaj.framework.spi.admin.auth.AuthenticationUser;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SaTokenSecurityService implements SecurityService {

    private final CaptchaService captchaService;
    private final FrameworkProperties properties;
    private final AuthorizationService authorizationService;
    private final AuthenticationService authenticationService;

    public SaTokenSecurityService(CaptchaService captchaService
            , FrameworkProperties properties, AuthorizationService authorizationService
            , AuthenticationService authenticationService) {
        this.properties = properties;
        this.captchaService = captchaService;
        this.authorizationService = authorizationService;
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean isLogin() {
        return StpUtil.isLogin();
    }

    @Override
    public Optional<Entity> getUser() {
        if(isLogin()) {
            TokenSign tokenSign = StpUtil.getSession().getTokenSignList().get(0);
            Entity user = (Entity) tokenSign.getTag();
            return Optional.ofNullable(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<String>> getRoles() {
        return getLoginId().map(item -> authorizationService.getRoles(item));
    }

    @Override
    public boolean hasRole(String role) {
        return StpUtil.hasRole(role);
    }

    @Override
    public boolean hasRole(Logical logical, String... roles) {
        if(logical == Logical.AND) {
            return StpUtil.hasRoleAnd(roles);
        } else {
            return StpUtil.hasRoleOr(roles);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return StpUtil.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(Logical logical, String... permissions) {
        if(logical == Logical.AND) {
            return StpUtil.hasPermissionAnd(permissions);
        } else {
            return StpUtil.hasPermissionOr(permissions);
        }
    }

    @Override
    public Collection<String> getSessionKeys() {
        return StpUtil.getSession().keys();
    }

    @Override
    public <T> Optional<T> getSessionAttr(String key) {
        return Optional.ofNullable((T) StpUtil.getSession().get(key));
    }

    @Override
    public SecurityService setSessionAttr(String key, Object value) {
        StpUtil.getSession().set(key, value);
        return this;
    }

    @Override
    public Object removeSessionAttr(String key) {
        Object o = StpUtil.getSession().get(key);
        StpUtil.getSession().delete(key);
        return o;
    }

    @Override
    public void logout(Serializable sessionId) {
        StpUtil.kickoutByTokenValue(sessionId.toString());
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        StpUtil.logout();
    }

    @Override
    public void login(SecurityToken token, HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        SaLoginModel saLoginModel;
        if(token instanceof OpenToken) {
            saLoginModel = createOpenLoginModel((OpenToken) token);
        } else {
            saLoginModel = createLoginModel(token);
        }


        // 如果配置了token写到header
        FrameworkProperties.Session session = properties.getWeb().getSession();
        if(session.isWriteHeader()) {
            saLoginModel.setIsWriteHeader(true);
        }

        UserAgent agent = (UserAgent) request.getAttribute(CoreConst.WEB_USER_AGENT);
        if(agent != null) {
            try {
                // 如果配置了自动选择token, app请求必须将token写到header
                if(session.isAutomaticToken() && agent.getBrowser().getBrowserType() == BrowserType.APP) {
                    saLoginModel.setIsWriteHeader(true);
                }

                saLoginModel.setDevice(agent.getOperatingSystem().getDeviceType().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Object byAccount = saLoginModel.getTokenSignTag();
        StpUtil.login(((Entity) byAccount).getId(), saLoginModel);
    }

    private SaLoginModel createOpenLoginModel(OpenToken token) {
        if(StrUtil.isBlank(token.getToken())) {
            throw new SecurityException("未指定token");
        }

        AuthenticationUser byAccount = this.authenticationService.getByAccount(token.getAccount());
        if(byAccount == null) {
            throw new SecurityException("账号不存在");
        }

        if(!byAccount.allowLogin()) {
            throw new SecurityException("账号已被禁用");
        }

        return new SaLoginModel()
                .setTokenSignTag(byAccount)
                .setToken(token.getToken());
    }

    private SaLoginModel createLoginModel(SecurityToken token) {
        String code = token.getCode();
        if(code == null) {
            throw new SecurityException("校验码校验失败");
        }

        // 获取校验码并判断是否过期
        Captcha serviceCaptcha = captchaService.removeCaptcha(code);
        if(serviceCaptcha == null || serviceCaptcha.isExpire()) {
            throw new SecurityException("验证码已失效");
        }

        if(!Objects.equals(serviceCaptcha.getCaptcha(), token.getCaptcha())) {
            throw new SecurityException("验证码校验失败");
        }

        AuthenticationUser byAccount = this.authenticationService.getByAccount(token.getAccount());
        if(byAccount == null) {
            throw new SecurityException("账号不存在");
        }

        if(!byAccount.allowLogin()) {
            throw new SecurityException("账号已被禁用");
        }

        String md5 = SecureUtil.md5(token.getPassword());
        if(!md5.equals(byAccount.getPassword())) {
            throw new SecurityException("密码不匹配");
        }

        return new SaLoginModel()
                .setTokenSignTag(byAccount)
                .setIsLastingCookie(token.isRememberMe());
    }

    @Override
    public boolean isCurrentUser(Object sessionId) {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Objects.equals(tokenInfo.tokenValue, sessionId);
    }
}
