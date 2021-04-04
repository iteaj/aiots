package com.iteaj.framework.spi.auth.handle;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.auth.WebAuthHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public AuthToken doLogin(AuthContext context, WebAuthAction action
            , HttpServletRequest request, HttpServletResponse response) {
        return action.login(context, request, response);
    }
}
