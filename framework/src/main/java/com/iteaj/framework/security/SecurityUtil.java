package com.iteaj.framework.security;

import com.iteaj.framework.Entity;
import com.iteaj.framework.consts.CoreConst;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public class SecurityUtil {

    private static SecurityUtil instance;
    private static SecurityService securityService;

    protected SecurityUtil(SecurityService securityService) {
        this.securityService = securityService;
    }

    public synchronized static SecurityUtil getInstance(SecurityService securityService) {
        if(SecurityUtil.instance == null) {
            instance = new SecurityUtil(securityService);
        }

        return SecurityUtil.instance;
    }

    /**
     * 判断当前用户是否登录
     * @return
     */
    public static boolean isLogin() {
        return securityService.isLogin();
    }

    /**
     * 是否是超级管理员
     * @return 是否超级管理员
     */
    public static boolean isSuper() {
        return getLoginId()
                .map(item -> item.equals(1l))
                .orElse(false);
    }

    /**
     * 是否是超级管理员
     * @return 是否超级管理员
     */
    public static boolean isSuper(Serializable loginId) {
        return getLoginId()
                .map(item -> item.equals(loginId))
                .orElse(false);
    }

    /**
     * @return 返回浏览器的Agent信息
     */
    public static UserAgent getAgent() {
        return (UserAgent) getRequest().getAttribute(CoreConst.WEB_USER_AGENT);
    }

    /**
     * @return 返回当前请求{@code HttpServletRequest}对象
     */
    public static HttpServletRequest getRequest() {
        return securityService.getRequest();
    }

    /**
     * @return 返回当前请求的{@code HttpServletResponse}对象
     */
    public static HttpServletResponse getResponse() {
        return securityService.getResponse();
    }

    /**
     * 从request里面获取对应的属性
     * @param key
     * @param <T>
     * @return
     */
    public static <T> Optional<T> getRequestAttr(String key) {
        return Optional.ofNullable((T) getRequest().getAttribute(key));
    }

    /**
     * 将名为key的一个属性放到request
     * @param key
     * @param value
     * @return
     */
    public static SecurityUtil setRequestAttr(String key, Object value) {
        getRequest().setAttribute(key, value);
        return SecurityUtil.instance;
    }

    /**
     * 返回session里面所有的属性
     * @return
     */
    public static Collection<String> getSessionKeys() {
        return securityService.getSessionKeys();
    }

    /**
     * 从session里面获取对应的属性
     * @param key
     * @param <T>
     * @return
     */
    public static <T> Optional<T> getSessionAttr(String key) {
        return securityService.getSessionAttr(key);
    }

    /**
     * 将名为key的一个属性放到session
     * @param key
     * @param value
     * @return
     */
    public static SecurityService setSessionAttr(String key, Object value) {
        return securityService.setSessionAttr(key, value);
    }

    public static Object removeSessionAttr(String key) {
        return securityService.removeSessionAttr(key);
    }

    /**
     * 获取当前登入用户
     * @return 用户id
     */
    public static Optional<Entity> getLoginUser() {
        return securityService.getUser();
    }

    /**
     * 获取当前登入用户的id
     * @return 用户id
     */
    public static Optional<Serializable> getLoginId() {
        return securityService.getLoginId();
    }

    /**
     * 注销
     * @param request
     * @param response
     */
    public static void logout(HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        securityService.logout(request, response);
    }

    /**
     * 登录
     * @param token
     * @param request
     * @param response
     */
    public static void login(SecurityToken token, HttpServletRequest request, HttpServletResponse response) throws SecurityException {
        securityService.login(token, request, response);
    }
}
