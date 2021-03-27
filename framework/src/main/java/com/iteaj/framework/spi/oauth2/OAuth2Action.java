package com.iteaj.framework.spi.oauth2;


import com.iteaj.framework.spi.app.AppAuthAction;
import com.iteaj.framework.spring.boot.config.FrameworkAppProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/22
 *  基于Oauth2授权时执行的动作
 * @author iteaj
 * @since 1.0
 */
public interface OAuth2Action extends AppAuthAction<OAuth2User> {

    /**
     * 将在系统发起授权请求, 且客户端同意授权的时候调用
     * 在OAuth2客户端发起redirectUri时调用
     * @see FrameworkAppProperties.Oauth2#getRedirectUri()
     * @param request
     * @param response
     */
    @Override
    void login(HttpServletRequest request, HttpServletResponse response);

    /**
     * 客户端授权成功并且获取用户信息成功时的回调
     * @param user
     * @param request 回调请求的请求对象
     * @param response 回调请求的响应对象
     */
    @Override
    void callBack(OAuth2User user, HttpServletRequest request, HttpServletResponse response);
}
