package com.iteaj.framework.security;

import com.iteaj.framework.Entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
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
