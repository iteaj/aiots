package com.iteaj.framework.spi.admin;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.admin.auth.AuthenticatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityUtils {

    private static AuthenticatingService authenticatingService;
    public static Logger LOGGER = LoggerFactory.getLogger("SecurityService");

    public SecurityUtils(AuthenticatingService authenticatingService) {
        SecurityUtils.authenticatingService = authenticatingService;
    }

    /**
     * 返回当前登录的用户
     * @return
     */
    public static BaseEntity getPrincipal() {
        return authenticatingService.getPrincipal();
    }

    /**
     * 是否已经认证登录
     * @return
     */
    public static boolean isLogin() {
        return getPrincipal() != null;
    }

    /**
     * 当前登录的用户是否是超级管理员
     * @return
     */
    public static boolean isSuper() {
        return getPrincipal().getId() == 1;
    }

    /**
     * 判断用户是不是超级管理员
     * @param id
     * @return
     */
    public static boolean isSuper(Long id) {
        return id == 1l;
    }

}
