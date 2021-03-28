package com.iteaj.framework.spi.auth;

import com.iteaj.framework.FrameworkException;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.app.AppAuthAction;

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

    public AccountAuthHandler(List<AppAuthAction> actions) {
        super(actions);
    }

    @Override
    protected void doHandle(AuthContext context, HttpServletRequest request, HttpServletResponse response) {

        /*原生app认证不做任何事*/
        if(context.getType() == AuthType.Account) {
            this.doAccountHandle(context, request, response);
        } else {
            this.doOauth2Handle(context, request, response);
        }
    }

    protected void doOauth2Handle(AuthContext context, HttpServletRequest request, HttpServletResponse response) {

    }

    protected void doAccountHandle(AuthContext context, HttpServletRequest request, HttpServletResponse response) {
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
            AppAuthAction authAction = getAction(action);
            if(null == authAction) {
                throw new FrameworkException("查找不到处理此次登录的动作[AppAuthAction]");
            }

            try {
                // 执行动作
                authAction.login(null, request, response);
            } catch (Exception e) {
                // 处理登录异常
                if(authAction instanceof ExceptionAction) {
                    ((ExceptionAction<?>) authAction).loginCause(e, request, response);
                }
            }
        } else {
            throw new FrameworkException("登录失败");
        }
    }
}
