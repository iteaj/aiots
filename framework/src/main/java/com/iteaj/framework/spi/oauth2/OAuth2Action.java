package com.iteaj.framework.spi.oauth2;


import com.iteaj.framework.spi.app.AppAuthAction;

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
     * 客户端授权成功并且获取code成功时的回调
     * @param user
     * @param request 回调请求的请求对象
     * @param response 回调请求的响应对象
     */
    @Override
    Void login(OAuth2User user, HttpServletRequest request, HttpServletResponse response);

}
