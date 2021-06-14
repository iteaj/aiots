package com.iteaj.core.auth;

import com.iteaj.core.entity.Admin;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.auth.AuthException;
import com.iteaj.framework.spi.auth.AuthToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * create time: 2021/3/30
 *  自动授权登录动作
 *  比如：在开发环境系统重启的时候无需再次输入用户名密码, 直接登录(但是必须session未过期)
 * @author iteaj
 * @since 1.0
 */
public class AdminAutoAuthAction extends AdminAuthAction {

    public AdminAutoAuthAction(String... urlPattern) {
        super(urlPattern);
    }

    @Override
    public boolean pathMatcher(String uri, HttpServletRequest request) {
        // 匹配后台的url模式, 说明此请求是后台请求
        if(!super.pathMatcher(uri, request)) {
            return false;
        }

        // 说明他是一个在线用户, 允许自动登录
        final HttpSession session = request.getSession();
        return session.getAttribute(CoreConst.ONLINE_ACCOUNT_ID) != null;
    }

    @Override
    public boolean preAuthorize(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 直接登录
            this.login(null, request, response);
            return false; // 登录成功后直接放行
        } catch (Exception e) {
            // 登录异常
            return loginCall(e, request, response);
        }
    }

    @Override
    public AuthToken login(AuthContext t, HttpServletRequest request, HttpServletResponse response) {
        final HttpSession session = request.getSession(false);
        try {
            final Object account = session.getAttribute(CoreConst.ONLINE_ACCOUNT_ID);

            final Admin admin = adminService.getByAccount((String) account);
            if(admin == null) {
                throw new AuthException("认证失败");
            }

            return getFrameworkAuthInfo(null, admin);
        } finally { // 此处必须移除, 否则将会自动登录
            session.removeAttribute(CoreConst.ONLINE_ACCOUNT_ID);
        }
    }

}
