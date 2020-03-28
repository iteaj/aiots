package com.iteaj.framework.authority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2020/3/4
 *  认证服务
 * @author iteaj
 * @since 1.0
 */
public interface AuthService {

    /**
     * 返回登录页面
     * @return
     */
    String login(HttpServletRequest request, HttpServletResponse response);

    /**
     * 验证登录
     * @param token
     */
    void login(AuthToken token, HttpServletRequest request, HttpServletResponse response);

    /**
     * 注销登录
     */
    String logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * 首页页面
     * @param request
     * @param response
     * @return
     */
    String index(HttpServletRequest request, HttpServletResponse response);
}
