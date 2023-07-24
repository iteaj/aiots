package com.iteaj.iboot.plugin.satoken.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import com.iteaj.framework.Entity;
import com.iteaj.framework.captcha.Captcha;
import com.iteaj.framework.captcha.CaptchaService;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.security.SecurityException;
import com.iteaj.framework.security.*;
import com.iteaj.framework.spi.admin.auth.AuthenticationUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class SaTokenSecurityService implements SecurityService {

    private final CaptchaService captchaService;
    private final AuthenticationService authenticationService;

    public SaTokenSecurityService(CaptchaService captchaService
            , AuthenticationService authenticationService) {
        this.captchaService = captchaService;
        this.authenticationService = authenticationService;
    }

    @Override
    public Optional<Entity> getUser() {
        Entity user = (Entity) StpUtil.getSession().get("user");
        return Optional.ofNullable(user);
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
    public void logout(HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        StpUtil.logout();
    }

    @Override
    public void login(SecurityToken token, HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        String code = SecurityUtil.getRequest().getParameter(CoreConst.CAPTCHA_CODE);
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

        String md5 = SecureUtil.md5(token.getPassword());
        if(!md5.equals(byAccount.getPassword())) {
            throw new SecurityException("密码不匹配");
        }

        SaLoginModel saLoginModel = new SaLoginModel()
                .setIsWriteHeader(true)
                .setIsLastingCookie(token.isRememberMe());
        StpUtil.login(((Entity) byAccount).getId(), saLoginModel);
        StpUtil.getSession().set("user", byAccount);
    }
}
