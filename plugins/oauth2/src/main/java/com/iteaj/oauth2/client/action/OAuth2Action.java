package com.iteaj.oauth2.client.action;


import com.iteaj.framework.spi.auth.AuthException;
import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.framework.spi.auth.WebAuthAction;
import com.iteaj.framework.spi.oauth2.OAuth2User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/22
 *  基于Oauth2授权时执行的动作
 * @author iteaj
 * @since 1.0
 */
public interface OAuth2Action extends WebAuthAction<OAuth2User> {

    @Override
    default boolean preAuthorize(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    /**
     * Oauth2直接返回false
     * @param request
     * @return
     */
    @Override
    default boolean isLoginRequest(HttpServletRequest request) {
        return false;
    }

    /**
     * 客户端授权成功并且获取code成功时的回调
     * @param user
     * @param request 回调请求的请求对象
     * @param response 回调请求的响应对象
     * @return 如果返回{@code null} 则取消登录
     */
    @Override
    AuthToken login(OAuth2User user, HttpServletRequest request, HttpServletResponse response) throws AuthException;

    /**
     * 处理Oauth2生成的url
     * @param url oauth2授权url
     * @param request
     * @param response
     * @return true 需要授权 false 不需要授权
     */
    boolean doAuthorize(String url, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override
    default boolean postAuthorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return true;
    }
}
