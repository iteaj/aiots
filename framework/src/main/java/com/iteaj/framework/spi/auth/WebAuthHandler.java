package com.iteaj.framework.spi.auth;

import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * create time: 2021/3/24
 *  web请求授权处理器 支持pc和移动端
 * @author iteaj
 * @since 1.0
 */
public interface WebAuthHandler {

    FrameworkProperties getConfig();

    /**
     * 通过Action名称获取一个Action
     * 在跳转到授权时会存储当前对应的Action名称到当前session
     * 在授权回调时, 通过session里面的此参数获取对应的Action对象
     * @param name
     * @return
     */
    WebAuthAction getAction(String name);

    /**
     * 获取当前系统所有注入到spring容器的对象<br>
     *     1. 一个系统可以有多个动作, 比如处理用账号密码登录的动作 比如用Oauth2授权动作
     * @return
     */
    List<WebAuthAction> getActions();

    /**
     * app端未认证时的处理方法
     * @param request
     * @param response
     */
    boolean handle(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * web登录时的处理
     * @see FrameworkAuthServlet#service(ServletRequest, ServletResponse)
     * @param request
     * @param response
     */
    void login(HttpServletRequest request, HttpServletResponse response) throws AuthException;

    /**
     * web注销时的处理
     * @see FrameworkAuthServlet#service(ServletRequest, ServletResponse)
     * @param request
     * @param response
     */
    void logout(HttpServletRequest request, HttpServletResponse response) throws AuthException;
}
