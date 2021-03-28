package com.iteaj.core.app;

import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.DefaultOAuth2Action;
import com.iteaj.framework.spi.oauth2.OAuth2User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class OAuth2WechatAction extends DefaultOAuth2Action {

    public OAuth2WechatAction(String... urlPattern) {
        super(urlPattern);
    }

    @Override
    public AuthContext authorize(HttpServletRequest request, HttpServletResponse response) {
        return AuthContext.build(AuthType.WechatMp);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public Void login(OAuth2User user, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
