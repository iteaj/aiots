package com.iteaj.framework.utils;

import com.iteaj.framework.BaseEntity;
import com.iteaj.util.JsonUtils;
import com.iteaj.util.module.json.Json;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import java.util.Map;

public class ShiroUtils {

    /**
     * 返回当前登录的用户
     * @return
     */
    public static BaseEntity getPrincipal() {
        return (BaseEntity) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 是否已经认证登录
     * @return
     */
    public static boolean isLogin() {
        return SecurityUtils.getSubject().isAuthenticated();
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
        return Long.compare(id, 1l) == 0;
    }

    /**
     * 注销当前登录
     */
    public static void logout() {
        if(isLogin()) SecurityUtils.getSubject().logout();
    }
}
