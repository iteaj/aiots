package com.iteaj.security.shiro.app;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.admin.auth.AuthToken;
import com.iteaj.framework.spi.app.AbstractAccountAuthAction;
import com.iteaj.framework.spi.app.AuthContext;
import com.iteaj.framework.spi.app.AuthType;
import com.iteaj.framework.spring.boot.config.FrameworkAppProperties;
import org.apache.shiro.SecurityUtils;
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
public class ShiroAppAccountAuthAction extends AbstractAccountAuthAction {

    public ShiroAppAccountAuthAction(String urlPattern) {
        super(urlPattern);
    }

    /**
     * 基于用户名和密码认证方式的登录操作
     * @param request
     * @param response
     */
    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) {
        FrameworkAppProperties.Account account = getConfig().getAccount();

        // 获取前端提交的用户名, 密码, 校验码等数据
        String code = request.getParameter(account.getCode());
        String userName = request.getParameter(account.getUserName());
        String password = request.getParameter(account.getPassword());

        try {
            AppAuthenticationToken token = new AppAuthenticationToken(code, userName, password);

            // 执行登录操作
            SecurityUtils.getSubject().login(token);

            // 认证成功时执行回调
            this.callBack(token, request, response);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } finally {

            // 登录后移除授权上下文
            HttpSession session = request.getSession();
            session.removeAttribute(CoreConst.APP_SESSION_AUTH_CONTEXT);
        }
    }

    @Override
    public AuthContext authorize(HttpServletRequest request, HttpServletResponse response) {
        // 以用户名和密码的方式授权
        return AuthContext.build(AuthType.Account);
    }

    @Override
    public void callBack(AuthToken token, HttpServletRequest request, HttpServletResponse response) {

    }
}
