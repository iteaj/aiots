package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.spi.auth.AuthType;

/**
 * 第三方 OAuth2 授权返回的用户信息(与具体授权库解耦的抽象结果)
 *
 * @author iteaj
 */
public interface Oauth2Result {

    /** 授权平台类型 */
    AuthType getType();

    /** 第三方用户唯一标识(用于本地绑定, 存 oauth2_user.openid) */
    String getOpenid();

    /** 第三方登录名 */
    String getUsername();

    /** 第三方昵称 */
    String getNickname();

    /** 第三方头像 */
    String getAvatar();

    /** 第三方邮箱(可能为空) */
    String getEmail();

    /**
     * 用户网址
     */
    String getBlog();
    /**
     * 所在公司
     */
    String getCompany();
    /**
     * 位置
     */
    String getLocation();
    /**
     * 用户令牌
     */
    String getToken();
    /**
     * 用户备注（各平台中的用户个人介绍）
     */
    String getRemark();
}
