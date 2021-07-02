package com.iteaj.framework.spi.auth.handle;

import com.iteaj.framework.FrameworkException;
import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.auth.*;
import com.iteaj.framework.autoconfigure.FrameworkWebProperties;
import com.iteaj.framework.web.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

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
public abstract class DefaultAuthHandler implements WebAuthHandler, InitializingBean {

    private List<WebAuthAction> actions;
    private FrameworkWebProperties config;
    private Map<String, WebAuthAction> actionMap;
    protected static Logger logger = LoggerFactory.getLogger(WebAuthHandler.class);

    public DefaultAuthHandler(List<WebAuthAction> actions) {
        this.actions = actions;
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
    public boolean isLoginRequest(HttpServletRequest request) {
        final WebAuthAction action = matcher(request);
        if(action != null) {
            return action.isLoginRequest(request) && request
                    .getMethod().equalsIgnoreCase("GET");
        }

        return false;
    }

    @Override
    public boolean isSubmitRequest(HttpServletRequest request) {
        final WebAuthAction action = matcher(request);
        if(action != null) {
            return action.isSubmitRequest(request) & request
                    .getMethod().equalsIgnoreCase("POST");
        }

        return false;
    }

    @Override
    public final WebAuthAction matcher(HttpServletRequest request) {
        final Object action = request.getAttribute(CoreConst.HANDLE_AUTH_ACTION);
        if(action != null) {
            return (WebAuthAction) action;
        }

        String requestURI = WebUtils.getRequestUriContext(request);
        for (WebAuthAction item: actions) {
            if(item.pathMatcher(requestURI, request)) {
                request.setAttribute(CoreConst.HANDLE_AUTH_ACTION, item);
                return item;
            }
        }

        return null;
    }

    @Override
    public List<WebAuthAction> getActions() {
        return this.actions;
    }

    public boolean postAuthorize(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {

        final WebAuthAction action = matcher(request);

        // 保存当前请求域
        context.put("domain", WebUtils.getContextRequestUrl(request));

        return action.postAuthorize(context, request, response);
    }

    @Override
    public AuthToken login(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws AuthException {
        final String action = context.getAction();
        final WebAuthAction authAction = getAction(action);

        AuthToken token = null;
        try {
            token = this.doLogin(context, authAction, request, response);
            authAction.loginCall(null, request, response);
        } catch (Exception e) {
            authAction.loginCall(e, request, response);
        }

        return token;
    }

    protected abstract AuthToken doLogin(AuthContext context, WebAuthAction authAction, HttpServletRequest request, HttpServletResponse response);

    /**
     * 注销当前用户
     * @param request
     * @param response
     * @throws AuthException
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws AuthException {
        final HttpSession session = request.getSession();
        final Object action = session.getAttribute(CoreConst.HANDLE_LOGIN_ACTION);
        if(action != null) {
            final WebAuthAction authAction = getAction((String) action);
            authAction.logout(request, response);
        } else {
            throw new AuthException("注销失败");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public FrameworkWebProperties getConfig() {
        return config;
    }

    @Autowired
    public DefaultAuthHandler setConfig(FrameworkWebProperties config) {
        this.config = config;
        return this;
    }
}
