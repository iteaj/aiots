package com.iteaj.framework.spi.admin;

import com.iteaj.framework.spi.admin.auth.FrameworkAuthService;
import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.util.module.mvc.orm.Entity;

public class SecurityUtils {

    private static FrameworkAuthService frameworkAuthService;

    public SecurityUtils(FrameworkAuthService frameworkAuthService) {
        SecurityUtils.frameworkAuthService = frameworkAuthService;
    }

    /**
     * 返回当前登录的用户
     * @return
     */
    public static <T extends Entity> T getUser() {
        return (T) frameworkAuthService.getUser();
    }

    public static WebAuthAction getAction() {
        return frameworkAuthService.getAction();
    }

    /**
     * 是否已经认证登录
     * @return
     */
    public static boolean isLogin() {
        return getUser() != null;
    }

    /**
     * 后台管理端：当前用户是否超级管理员
     * @return
     */
    public static boolean isSuper() {
        if(getUser().getId() instanceof Number) {
            return ((Number) getUser().getId()).longValue() == 1;
        }

        return false;
    }

    /**
     * 后台管理端：验证用户是不是超级管理员
     * @param id
     * @return
     */
    public static boolean isSuper(Long id) {
        return id == 1l;
    }

}
