package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.spi.auth.AuthException;
import com.iteaj.framework.autoconfigure.FrameworkWebProperties;
import com.iteaj.util.module.mvc.HttpResult;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * create time: 2021/3/27
 *  根据客户端类型自动选择OAuth2授权动作
 * @author iteaj
 * @since 1.0
 */
public abstract class DefaultOAuth2Action implements OAuth2Action {

    private String[] urlPattern;
    private FrameworkWebProperties config;

    public DefaultOAuth2Action(String[] urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public String getLoginUrl() {
        return null;
    }

    /**
     * Oauth2回调后登录成功时跳转的地址
     * @return
     */
    public String getSuccessUrl() {
        return null;
    }

    /**
     * Oauth2
     * @param request
     * @return
     */
    @Override
    public boolean isSubmitRequest(HttpServletRequest request) {
        return false;
    }

    @Override
    public String[] getUrlPattern() {
        return this.urlPattern;
    }

    @Override
    public boolean doAuthorize(String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(isAjax(request)) {
            // 写出结果
            writeJson(HttpResult.Success(url,"生成授权url成功"), response);
        } else {

            // 重定向Oauth2授权地址到授权服务器
            response.sendRedirect(url);
        }

        return true;
    }

    @Override
    public boolean loginCall(Exception cause, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 登录成功重定向到成功页面
            if(cause == null) {
                response.sendRedirect(getSuccessUrl());
            } else {
                writeJson(HttpResult.Fail("登录失败"), response);
            }
        } catch (IOException e) {
            throw new AuthException(e);
        }

        return true;
    }

    public DefaultOAuth2Action setUrlPattern(String[] urlPattern) {
        this.urlPattern = urlPattern;
        return this;
    }

    public FrameworkWebProperties getConfig() {
        return config;
    }

    @Autowired
    public DefaultOAuth2Action setConfig(FrameworkWebProperties config) {
        this.config = config;
        return this;
    }
}
