package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.auth.AuthContext;
import com.iteaj.framework.spi.auth.AuthException;
import com.iteaj.framework.spi.auth.DefaultAuthHandler;
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
public abstract class AbstractOAuth2Handler extends DefaultAuthHandler {

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
                }
            } else {
                logger.error("找不到匹配的[WebAuthAction]来处理此处请求["+
                        request.getRequestURI()+"]", new AuthException((String) null));
            }
        } catch (Exception e) {
            logger.error("Oauth2授权回调处理失败", e);
        } finally {
            if(context != null) {
                session.removeAttribute(CoreConst.SESSION_AUTH_CONTEXT);
            }
        }
    }

    protected abstract void doOauth2Login(AuthContext authContext, HttpServletRequest request, HttpServletResponse response);
}
