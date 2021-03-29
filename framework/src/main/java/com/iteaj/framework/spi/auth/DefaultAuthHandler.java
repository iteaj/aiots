package com.iteaj.framework.spi.auth;

import com.iteaj.framework.FrameworkException;
import com.iteaj.framework.Proxy.ProxyUtils;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create time: 2021/3/24
 *  默认的移动端处理器
 * @author iteaj
 * @since 1.0
 */
public abstract class DefaultAuthHandler implements WebAuthHandler {

    private PathMatcher pathMatcher;
    private List<WebAuthAction> actions;
    private FrameworkProperties config;
    private Map<String, WebAuthAction> actionMap;
    protected static Logger logger = LoggerFactory.getLogger(WebAuthHandler.class);

    public DefaultAuthHandler(List<WebAuthAction> actions) {
        this.actions = actions;
        this.pathMatcher = new AntPathMatcher();
        try {
            this.actionMap = actions.stream().collect(Collectors
                    .toMap(item -> item.getName(), item -> item));
        } catch (Exception e) {
            throw new FrameworkException("actions必填或者actions名称重复", e);
        }
    }

    @Override
    public WebAuthAction getAction(String name) {
        return actionMap.get(name);
    }

    @Override
    public List<WebAuthAction> getActions() {
        return this.actions;
    }

    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestURI = request.getRequestURI();
        requestURI = requestURI.substring(request.getContextPath().length());

        if(this.getActions().size() == 0) {
            logger.warn("移动端授权处理器未指定任何动作[{}]", getClass().getName());
        } else {
            WebAuthAction action = null; String urlPattern = "";
            for (WebAuthAction item: this.getActions()) {
                for (String url : item.getUrlPattern()) {
                    if(this.pathMatcher.match(url, requestURI)) {
                        action = item; urlPattern = url;
                        break;
                    }
                }

                if(action != null) {
                    break;
                }
            }

            if(action != null) {
                if(logger.isDebugEnabled()) {
                    logger.debug("路径["+ request.getRequestURI() +"]匹配到授权动作["
                            + action.getName()+"("+ urlPattern +")]");
                }

                // 授权前的校验
                if (!preAuthorize(request, response, action)) {
                    return true;
                }

                // 执行授权
                AuthContext authContext = action.authorize(request, response);
                if(authContext == null) { // 终止授权
                    return false;
                }

                if(authContext.getType() == null) {
                    throw new AuthException("认证类型必填[AuthType]");
                }

                postAuthorize(request, authContext, action);

                // 继续执行授权
                this.doAuthorize(action, authContext, request, response);

                saveAuthContextToSession(request, authContext);
                return false;
            } else {
                /**
                 * 说明此次授权没有找到对应的动作{@link WebAuthAction}<br>
                 *     或者动作没有返回任何处理方式{@link AuthType}
                 */
                logger.warn("此次请求[{}]没有匹配到任何授权动作[{}]", requestURI, getActions());
                return true; // 没有匹配到任何处理动作, 直接放行
            }
        }

        return false;
    }

    private void saveAuthContextToSession(HttpServletRequest request, AuthContext authContext) {
        // 保存此上下文到session(必须在此处写入到session, 否则可能会出现数据没有缓存的情况)
        HttpSession session = request.getSession();
        session.setAttribute(CoreConst.SESSION_AUTH_CONTEXT, authContext);
    }

    /**
     * 是否需要授权
     * @param request
     * @param response
     * @param item
     * @return true 代表是  false 代表否
     */
    protected boolean preAuthorize(HttpServletRequest request
            , HttpServletResponse response, WebAuthAction item) {

        return true;
    }

    protected void postAuthorize(HttpServletRequest request, AuthContext authContext, WebAuthAction item) {

        /**
         * 必须保存此次动作对象{@link WebAuthAction}到认证上下文{@link AuthContext}<br>
         *     在提交登录的时候必须再次获取此动作进行登录处理
         * @see #login(HttpServletRequest, HttpServletResponse)
         */
        authContext.setAction(item.getName());

        // 保存授权前请求的url
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if(queryString != null) {
            authContext.put("url", requestURL.toString() + "?" + queryString);
        } else {
            authContext.put("url", requestURL.toString());
        }

        // 保存当前请求域
        String domain = requestURL.substring(0, requestURL
                .length() - request.getRequestURI().length());
        final String contextPath = request.getContextPath();
        authContext.put("domain", domain + contextPath);
    }

    protected abstract void doAuthorize(WebAuthAction authAction, AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 登录回调处理
     * @param authAction
     * @param cause
     * @param request
     * @param response
     */
    protected void loginCallHandle(WebAuthAction authAction, Exception cause, HttpServletRequest request, HttpServletResponse response) {
        final Object target = ProxyUtils.getTarget(authAction);
        final HttpSession session = request.getSession();
        // 处理登录异常
        if(target instanceof AuthCallAction) {
            ((AuthCallAction<?>) target).loginCall(cause, request, response);
        }

        // 移除授权上下文
        session.removeAttribute(CoreConst.SESSION_AUTH_CONTEXT);

        // 登录成功, 保存当前用户授权的action
        if(cause == null) {
            // 保存处理当前授权动作的动作明此
            session.setAttribute(CoreConst.AUTH_ACTION_NAME_CURRENT, authAction.getName());
        }
    }

    /**
     * 注销当前用户
     * 注销请求将交给原先授权登录的动作{@link WebAuthAction}
     * @param request
     * @param response
     * @throws AuthException
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws AuthException {
        Object actionName = request.getSession().getAttribute(CoreConst.AUTH_ACTION_NAME_CURRENT);
        if(actionName instanceof String) {
            WebAuthAction action = getAction((String) actionName);
            action.logout(request, response);
        }
    }

    public FrameworkProperties getConfig() {
        return config;
    }

    @Autowired
    public DefaultAuthHandler setConfig(FrameworkProperties config) {
        this.config = config;
        return this;
    }
}
