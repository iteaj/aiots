package com.iteaj.security.shiro.app;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.AccountAuthAction;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spring.boot.config.AppConfig;
import org.apache.shiro.authc.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * create time: 2021/3/26
 *  基于Shiro实现的通过用户名和密码认证的动作
 * @author iteaj
 * @since 1.0
 */
public class AppAccountAuthAction extends AccountAuthAction {

    public AppAccountAuthAction(String urlPattern) {
        super(urlPattern);
    }

    /**
     * 基于用户名和密码认证方式的登录操作
     * @param request
     * @param response
     */
    @Override
    public AuthToken login(Void t, HttpServletRequest request, HttpServletResponse response) {
        AppConfig.Account account = getConfig().getApp().getAccount();

        // 获取前端提交的用户名, 密码, 校验码等数据
        String code = request.getParameter(account.getCode());
        String userName = request.getParameter(account.getUserName());
        String password = request.getParameter(account.getPassword());

        try {
            return new AppAuthenticationToken(code, userName, password);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        } finally {

            // 登录后移除授权上下文
            HttpSession session = request.getSession();
            session.removeAttribute(CoreConst.APP_SESSION_AUTH_CONTEXT);
        }
    }

}
