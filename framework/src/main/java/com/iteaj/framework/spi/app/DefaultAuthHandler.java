package com.iteaj.framework.spi.app;

import com.iteaj.framework.FrameworkException;
import com.iteaj.framework.consts.CoreConst;
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
public abstract class DefaultAuthHandler implements AppAuthHandler {

    private PathMatcher pathMatcher;
    private List<AppAuthAction> actions;
    private FrameworkProperties config;
    private Map<String, AppAuthAction> actionMap;
    protected static Logger logger = LoggerFactory.getLogger(AppAuthHandler.class);

    public DefaultAuthHandler(List<AppAuthAction> actions) {
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
    public AppAuthAction getAction(String name) {
        return actionMap.get(name);
    }

    @Override
    public List<AppAuthAction> getActions() {
        return this.actions;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();

        if(this.getActions().size() == 0) {
            logger.warn("移动端授权处理器未指定任何动作[{}]", getClass().getName());
        } else {
            AuthContext authContext = null;
            for (AppAuthAction item: this.getActions()) {
                boolean match = this.pathMatcher.match(item.getUrlPattern(), requestURI);
                if(match) {
                    if(logger.isDebugEnabled()) {
                        logger.debug("路径["+requestURI+"]匹配到授权动作["
                                +item.getClass().getSimpleName()+"("+item.getUrlPattern()+")]");
                    }

                    authContext = item.authorize(request, response);
                    if(authContext == null) {
                        throw new AuthException("动作["+item.getName()
                                +".authorize()]没有返回指定的对象[AuthContext]");
                    }

                    if(authContext.getType() == null) {
                        throw new FrameworkException("认证类型必填[AuthType]");
                    }

                    /**
                     * 必须保存此次动作对象{@link AppAuthAction}到认证上下文{@link AuthContext}<br>
                     *     在提交登录的时候必须再次获取此动作进行登录处理
                     * @see #login(HttpServletRequest, HttpServletResponse)
                     */
                    authContext.setAction(item.getName());

                    // 保存授权前请求的url
                    StringBuffer requestURL = request.getRequestURL();
                    String queryString = request.getQueryString();
                    authContext.put("url", requestURL.toString() + "?" + queryString);

                    // 保存当前请求域
                    String domain = requestURL.substring(0, requestURL
                            .length() - request.getRequestURI().length());
                    authContext.put("domain", domain);

                    // 保存此上下文到session
                    HttpSession session = request.getSession();
                    session.setAttribute(CoreConst.APP_SESSION_AUTH_CONTEXT, authContext);
                    break;
                }
            }

            if(authContext != null) {
                this.doHandle(authContext, request, response);
            } else {
                /**
                 * 说明此次授权没有找到对应的动作{@link AppAuthAction}<br>
                 *     或者动作没有返回任何处理方式{@link AuthType}
                 */
                throw new AuthException("此次请求["+requestURI
                        +"]没有匹配到任何动作["+getActions()+"]");
            }
        }
    }

    protected abstract void doHandle(AuthContext context, HttpServletRequest request, HttpServletResponse response);

    public FrameworkProperties getConfig() {
        return config;
    }

    @Autowired
    public DefaultAuthHandler setConfig(FrameworkProperties config) {
        this.config = config;
        return this;
    }
}
