package com.iteaj.framework.spi.auth;

import com.iteaj.framework.autoconfigure.FrameworkWebProperties;

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

    FrameworkWebProperties getConfig();

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
     * 是否是登录页面请求
     * @param request
     * @return
     */
    boolean isLoginRequest(HttpServletRequest request);

    /**
     * 是否是提交登录请求
     * @param request
     * @return
     */
    boolean isSubmitRequest(HttpServletRequest request);

    /**
     * 返回匹配的动作
     * @param request
     * @return
     */
    WebAuthAction matcher(HttpServletRequest request);

    /**
     * web登录时的处理
     * @see FrameworkAuthServlet#service(ServletRequest, ServletResponse)
     * @param context
     * @param request
     * @param response
     */
    AuthToken login(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws AuthException;

    /**
     * web注销时的处理
     * @see FrameworkAuthServlet#service(ServletRequest, ServletResponse)
     * @param request
     * @param response
     */
    void logout(HttpServletRequest request, HttpServletResponse response) throws AuthException;

    /**
     * 继续授权
     * @param context
     * @param request
     * @param response
     * @return
     */
    boolean postAuthorize(AuthContext context, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
