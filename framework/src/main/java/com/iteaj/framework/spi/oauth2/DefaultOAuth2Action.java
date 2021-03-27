package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.spi.app.AuthContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/27
 *  根据客户端类型自动选择OAuth2授权动作
 * @author iteaj
 * @since 1.0
 */
public abstract class DefaultOAuth2Action implements OAuth2Action{

    private String urlPattern;

    public DefaultOAuth2Action(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public String getUrlPattern() {
        return this.urlPattern;
    }

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) {
        /*doing nothing*/
    }

    @Override
    public AuthContext authorize(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public DefaultOAuth2Action setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
        return this;
    }
}
