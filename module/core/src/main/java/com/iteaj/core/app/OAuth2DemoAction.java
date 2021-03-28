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
public class OAuth2DemoAction extends DefaultOAuth2Action {

    public OAuth2DemoAction(String... urlPattern) {
        super(urlPattern);
    }

    @Override
    public String successUrl() {
        return null;
    }

    @Override
    public AuthContext authorize(HttpServletRequest request, HttpServletResponse response) {
        return AuthContext.build(AuthType.Gitee);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public Void login(OAuth2User user, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
