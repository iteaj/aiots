package com.iteaj.framework.spi.oauth2;


import com.iteaj.framework.spi.app.WebAuthAction;
import com.iteaj.framework.spi.auth.AuthException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/22
 *  基于Oauth2授权时执行的动作
 * @author iteaj
 * @since 1.0
 */
public interface OAuth2Action extends WebAuthAction<OAuth2User> {

    /**
     * Oauth2直接返回false
     * @param requestUri
     * @param request
     * @return
     */
    @Override
    default boolean isLoginRequest(String requestUri, HttpServletRequest request) {
        return false;
    }

    /**
     * 客户端授权成功并且获取code成功时的回调
     * @param user
     * @param request 回调请求的请求对象
     * @param response 回调请求的响应对象
     */
    @Override
    Void login(OAuth2User user, HttpServletRequest request, HttpServletResponse response) throws AuthException;

    /**
     * 处理Oauth2生成的url
     * @param url oauth2授权url
     * @param request
     * @param response
     */
    void doAuthorize(String url, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
