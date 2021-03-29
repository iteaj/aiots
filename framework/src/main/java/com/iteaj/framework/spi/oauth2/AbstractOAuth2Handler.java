package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.Proxy.ProxyUtils;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.auth.*;
import org.springframework.cache.CacheManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    protected boolean preAuthorize(HttpServletRequest request, HttpServletResponse response, WebAuthAction item) {
        final Object target = ProxyUtils.getTarget(item);
        if(target instanceof AccountAuthAction) { // 用户名密码认证动作交给父处理
            return super.preAuthorize(request, response, item);
        } else {
            return true; // OAuth2必须要执行授权
        }
    }

    @Override
    protected void doAuthorize(WebAuthAction authAction, AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Object target = ProxyUtils.getTarget(authAction);
        if(target instanceof OAuth2Action) {
            oauth2Authorize((OAuth2Action)target, context, request, response);
        } else {
            super.doAuthorize(authAction, context, request, response);
        }
    }

    protected abstract void oauth2Authorize(OAuth2Action action, AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Object context = session.getAttribute(CoreConst.SESSION_AUTH_CONTEXT);
        try {
            // session存在认证上下文
            if(context instanceof AuthContext) {
                final String action = ((AuthContext) context).getAction();
                final WebAuthAction authAction = getAction(action);

                try {
                    doOauth2Login((AuthContext) context, request, response);
                    this.loginCallHandle(authAction, null, request, response);
                } catch (Exception e) {
                    this.loginCallHandle(authAction, e, request, response);
                    throw e;
                }
            } else {
                logger.error("找不到匹配的[WebAuthAction]来处理此请求[{}], 上下文已释放", request.getRequestURI());
            }
        } catch (Exception e) {
            logger.error("Oauth2授权回调处理失败", e);
        }
    }

    protected abstract void doOauth2Login(AuthContext authContext, HttpServletRequest request, HttpServletResponse response);
}
