package com.iteaj.core.auth;

import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.AuthException;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.DefaultOAuth2Action;
import com.iteaj.framework.spi.oauth2.OAuth2Action;
import com.iteaj.framework.spi.oauth2.OAuth2User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/4/5
 *
 * @author iteaj
 * @since 1.0
 */
public class AdminOAuth2Action extends DefaultOAuth2Action {

    public AdminOAuth2Action(String[] urlPattern) {
        super(urlPattern);
    }

    @Override
    public AuthContext authorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String type = request.getParameter("type");
        if(type == null) {
            throw new AuthException("未指定授权类型[type=Gitee]");
        }
        final AuthType authType = AuthType.valueOf(type);
        return AuthContext.build(authType);
    }

    @Override
    public String getSuccessUrl() {
        return null;
    }

    @Override
    public AuthToken login(OAuth2User user, HttpServletRequest request, HttpServletResponse response) throws AuthException {
        return null;
    }
}
