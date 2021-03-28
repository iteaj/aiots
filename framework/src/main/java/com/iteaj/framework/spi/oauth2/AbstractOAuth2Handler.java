package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.AccountAuthHandler;
import com.iteaj.framework.spi.app.AppAuthAction;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.AuthType;
import org.springframework.cache.Cache;
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

    public AbstractOAuth2Handler(List<AppAuthAction> actions, CacheManager cacheManager) {
        super(actions);
        this.cacheManager = cacheManager;
    }

    /**
     * 刷新access token （续期）
     *
     */
    protected abstract OAuth2Token refresh(String clientId);

    @Override
    public void login(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Object context = session.getAttribute(CoreConst.SESSION_AUTH_CONTEXT);

        // 用户名密码登录
        if(context instanceof AuthContext &&
                ((AuthContext) context).getType() == AuthType.Account) {

            super.login(request, response);
        } else { // Oauth2授权登录
            String state = request.getParameter("state");
            String code = request.getParameter("code");
            String authCode = request.getParameter("auth_code");
            Cache cache = this.cacheManager.getCache(CoreConst.OAUTH2_STATE_CACHE);

            try {
                AuthContext authContext = cache.get(state, AuthContext.class);
                if(authContext != null) {
                    // 获取执行动作
                    String action = authContext.getAction();
                    AppAuthAction authAction = getAction(action);
                    authContext.put("state", state).put("code", code).put("authCode", authCode);

                    doOauth2Login(authContext, request, response);
                }
            } catch (Exception e) {

            } finally {
                cache.evictIfPresent(state);
            }
        }
    }

    protected abstract void doOauth2Login(AuthContext authContext, HttpServletRequest request, HttpServletResponse response);
}
