package com.iteaj.framework.spi.auth;

import com.iteaj.framework.FrameworkException;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.app.WebAuthAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * create time: 2021/3/24
 *  普通的账号密码认证
 * @author iteaj
 * @since 1.0
 */
public class AccountAuthHandler extends DefaultAuthHandler {

    public AccountAuthHandler(List<WebAuthAction> actions) {
        super(actions);
    }

    @Override
    protected boolean preAuthorize(HttpServletRequest request, HttpServletResponse response, WebAuthAction item) {

        // 登录请求允许访问
        if(item.isLoginRequest(request.getRequestURI(), request)) {

            // 处理登录表单提交
            if(request.getMethod().equalsIgnoreCase("POST")) {
                try {
                    item.login(null, request, response);
                    this.loginCallHandle(item, null, request, response);
                } catch (Exception e) {
                    this.loginCallHandle(item, e, request, response);
                }
            } else { // 登录页面无需授权
                return false;
            }
        }

        return true;
    }

    @Override
    protected void doHandle(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {

        /*原生app认证不做任何事*/
        if(context.getType() == AuthType.Account) {
            this.doAccountHandle(context, request, response);
        } else {
            this.doOauth2Handle(context, request, response);
        }
    }

    protected void doOauth2Handle(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {

    }

    protected void doAccountHandle(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        /*doing nothing*/
    }

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        /**
         * 此上下文在认证拦截前已经放入session
         * @see CoreConst#SESSION_AUTH_CONTEXT
         */
        Object context = session.getAttribute(CoreConst.SESSION_AUTH_CONTEXT);
        if(context instanceof AuthContext) {
            String action = ((AuthContext) context).getAction();
            /**
             * 获取处理此次登录的动作
             * @see #handle(HttpServletRequest, HttpServletResponse)
             */
            WebAuthAction authAction = getAction(action);

            try {
                // 执行登录动作
                authAction.login(context, request, response);

                this.loginCallHandle(authAction, null, request, response);
            } catch (Exception e) {
                this.loginCallHandle(authAction, e, request, response);
            }
        } else {
            throw new FrameworkException("登录失败");
        }
    }
}
