package com.iteaj.iboot.plugin.satoken.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.iteaj.framework.Entity;
import com.iteaj.framework.security.AuthenticationService;
import com.iteaj.framework.security.SecurityException;
import com.iteaj.framework.security.SecurityService;
import com.iteaj.framework.security.SecurityToken;
import com.iteaj.framework.spi.admin.auth.AuthenticationUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class SaTokenSecurityService implements SecurityService {

    private final AuthenticationService authenticationService;

    public SaTokenSecurityService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Optional<Entity> getUser() {
        Entity user = (Entity) StpUtil.getSession().get("user");
        return Optional.ofNullable(user);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        StpUtil.logout();
    }

    @Override
    public void login(SecurityToken token, HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        AuthenticationUser byAccount = this.authenticationService.getByAccount(token.getAccount());
        if(byAccount == null) {
            throw new SecurityException("账号不存在");
        }

        StpUtil.login(token.getAccount(), token.isRememberMe());
        StpUtil.getSession().set("user", byAccount);
    }
}
