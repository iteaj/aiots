package com.iteaj.iboot.plugin.oauth2.service;

import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.Oauth2Result;
import me.zhyd.oauth.model.AuthUser;

/**
 * JustAuth {@link AuthUser} 到框架 {@link Oauth2Result} 的适配实现
 *
 * @author iteaj
 */
public class JustAuthOauth2Result implements Oauth2Result {

    private final AuthType type;
    private final AuthUser user;

    public JustAuthOauth2Result(AuthType type, AuthUser user) {
        this.type = type;
        this.user = user;
    }

    @Override
    public AuthType getType() {
        return type;
    }

    @Override
    public String getOpenid() {
        return user.getUuid();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getNickname() {
        return user.getNickname();
    }

    @Override
    public String getAvatar() {
        return user.getAvatar();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public String getBlog() {
        return user.getBlog();
    }

    @Override
    public String getCompany() {
        return user.getCompany();
    }

    @Override
    public String getLocation() {
        return user.getLocation();
    }

    @Override
    public String getToken() {
        return user.getToken().getAccessToken();
    }

    @Override
    public String getRemark() {
        return user.getRemark();
    }
}
