package com.iteaj.iboot.plugin.oauth2.entity;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.auth.AuthType;
import com.iteaj.framework.spi.oauth2.OAuth2ClientUser;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Oauth2ClientUser extends BaseEntity implements OAuth2ClientUser {

    /**
     * 第三方平台类型
     */
    private AuthType type;
    /**
     * 当前平台用户账号
     */
    private String account;

    /**
     * 三方平台用户openid
     */
    private String openid;

    /**
     * 第三方平台用户昵称
     */
    private String nickname;

    /**
     * 第三方平台用户头像
     */
    private String avatarUrl;

    /**
     * 第三方平台用户类型
     */
    private String userType;
}
