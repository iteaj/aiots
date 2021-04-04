package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.auth.handle.AccountAuthHandler;
import org.springframework.cache.CacheManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * create time: 2021/3/25
 *  Oauth2授权处理器
 * @author iteaj
 * @since 1.0
 */
public abstract class AbstractOAuth2Handler extends AccountAuthHandler {

    protected CacheManager cacheManager;

    public AbstractOAuth2Handler(List<WebAuthAction> actions, CacheManager cacheManager) {
        super(actions);
        this.cacheManager = cacheManager;
    }

    /**
     * 刷新access token （续期）
     *
     */
    protected abstract OAuth2Token refresh(String clientId);

    @Override
    public boolean postAuthorize(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {

        final WebAuthAction action = getAction(context.getAction());
        if(action instanceof OAuth2Action) {
            /**
             * 保存上下文到session, 在客户端重定向之后会再次获取此上下文
             * @see #handle(HttpServletRequest, HttpServletResponse)
             * @see com.iteaj.framework.spi.auth.WebAuthHandler#login(AuthContext, HttpServletRequest, HttpServletResponse)
             */
            request.getSession().setAttribute(CoreConst.SESSION_AUTH_CONTEXT, context);

            return oauth2Authorize((OAuth2Action)action, context, request, response);
        } else {
            return super.postAuthorize(context, request, response);
        }
    }

    protected abstract boolean oauth2Authorize(OAuth2Action action, AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override
    public AuthToken doLogin(AuthContext context, WebAuthAction action, HttpServletRequest request, HttpServletResponse response) {
        if(action instanceof OAuth2Action) {
            final OAuth2User oAuth2User = doOauth2Login(context, request, response);
            return action.login(oAuth2User, request, response);
        } else {
            return super.doLogin(context, action, request, response);
        }

    }

    protected abstract OAuth2User doOauth2Login(AuthContext authContext, HttpServletRequest request, HttpServletResponse response);
}
