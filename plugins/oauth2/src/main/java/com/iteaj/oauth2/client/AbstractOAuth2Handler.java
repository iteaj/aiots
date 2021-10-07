package com.iteaj.oauth2.client;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.*;
import com.iteaj.framework.spi.auth.SecurityException;
import com.iteaj.framework.spi.auth.handle.AccountAuthHandler;
import com.iteaj.oauth2.client.action.OAuth2Action;
import com.iteaj.framework.spi.oauth2.OAuth2Token;
import com.iteaj.framework.spi.oauth2.OAuth2User;
import com.iteaj.framework.web.WebUtils;
import com.iteaj.oauth2.OAuth2ClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
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
    private OAuth2ClientProperties oauth2Config;

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
    public boolean postAuthorize(HttpServletRequest request, HttpServletResponse response) throws SecurityException {

        final WebAuthAction action = this.matcher(request);
        if(action instanceof OAuth2Action) {
            final AuthContext context = getAuthContext(request);

            // 保存当前请求域
            context.put("domain", WebUtils.getContextRequestUrl(request));

            /**
             * 保存上下文到session, 在客户端重定向之后会再次获取此上下文
             * @see #handle(HttpServletRequest, HttpServletResponse)
             * @see com.iteaj.framework.spi.auth.WebAuthHandler#login(AuthContext, HttpServletRequest, HttpServletResponse)
             */
            request.getSession().setAttribute(CoreConst.SESSION_AUTH_CONTEXT, context);
            return oauth2Authorize((OAuth2Action)action, context, request, response);
        } else {
            return super.postAuthorize(request, response);
        }
    }

    protected abstract boolean oauth2Authorize(OAuth2Action action, AuthContext context
            , HttpServletRequest request, HttpServletResponse response) throws SecurityException;

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        if(isRedirectRequest(request)) {
            final AuthContext context = (AuthContext)request
                    .getSession().getAttribute(CoreConst.SESSION_AUTH_CONTEXT);

            if(context != null) {
                final String action = context.getAction();
                final WebAuthAction authAction = getAction(action);
                if(authAction instanceof OAuth2Action) {
                    Exception exception = null;
                    try {
                        final OAuth2User oAuth2User = doOauth2Login(context, request, response);
                        authAction.login(oAuth2User, request, response);
                    } catch (Exception e) {
                        exception = e;
                    } finally {
                        authAction.loginCall(exception, request, response);
                    }
                } else {
                    throw new SecurityException("Oauth2必须使用[OAuth2Action]");
                }
            } else {
                throw new SecurityException("找不到[AuthContext]可能已释放, 请重新授权");
            }
        } else {
            super.login(request, response);
        }
    }

    protected boolean isRedirectRequest(HttpServletRequest request) {
        final Object status = request.getAttribute(CoreConst.OAUTH2_REDIRECT_REQUEST);
        return status == null ? false : (boolean) status;
    }

    protected String getRedirectUrl() {
        return getOauth2Config().getRedirectUri();
    }

    protected abstract OAuth2User doOauth2Login(AuthContext authContext, HttpServletRequest request, HttpServletResponse response);

    public OAuth2ClientProperties getOauth2Config() {
        return oauth2Config;
    }

    @Autowired
    public AbstractOAuth2Handler setOauth2Config(OAuth2ClientProperties oauth2Config) {
        this.oauth2Config = oauth2Config;
        return this;
    }
}
