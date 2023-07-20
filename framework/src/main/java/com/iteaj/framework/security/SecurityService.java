package com.iteaj.framework.security;

import com.iteaj.framework.Entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Optional;

public interface SecurityService {

    /**
     * 当前用户是否登录
     * @return 是否登录
     */
    default boolean isLogin() {
        return getUser().isPresent();
    }

    /**
     * 获取当前登录用户
     * @return 用户对象
     */
    Optional<Entity> getUser();

    /**
     * 获取当前登入的用户id
     * @return 登录用户id
     */
    default Optional<Serializable> getLoginId() {
        return getUser().map(item -> item.getId());
    }

    /**
     * 注销
     * @param request
     * @param response
     */
    void logout(HttpServletRequest request, HttpServletResponse response) throws SecurityException;

    /**
     * 登录
     * @param token
     * @param request
     * @param response
     */
    void login(SecurityToken token, HttpServletRequest request, HttpServletResponse response) throws SecurityException;
}
