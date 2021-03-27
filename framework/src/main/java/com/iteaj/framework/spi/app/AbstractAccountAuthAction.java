package com.iteaj.framework.spi.app;

import com.iteaj.framework.spi.admin.auth.AuthToken;
import com.iteaj.framework.spring.boot.config.FrameworkAppProperties;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/25
 *  用于用户名和密码授权的认证动作
 * @author iteaj
 * @since 1.0
 */
public abstract class AbstractAccountAuthAction implements AppAuthAction<AuthToken> {

    private String urlPattern;
    private FrameworkAppProperties config;

    public AbstractAccountAuthAction(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public String getUrlPattern() {
        return this.urlPattern;
    }

    @Override
    public int getOrder() {
        return 88;
    }

    /**
     * 基于用户名和密码登录
     * @param request
     * @param response
     */
    @Override
    public abstract void login(HttpServletRequest request, HttpServletResponse response);

    @Override
    public abstract void callBack(AuthToken token, HttpServletRequest request, HttpServletResponse response);

    public AbstractAccountAuthAction setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
        return this;
    }

    public FrameworkAppProperties getConfig() {
        return config;
    }

    @Autowired
    public AbstractAccountAuthAction setConfig(FrameworkAppProperties config) {
        this.config = config;
        return this;
    }
}
