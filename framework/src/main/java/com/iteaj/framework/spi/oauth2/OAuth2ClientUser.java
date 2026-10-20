package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.spi.auth.AuthType;

public interface OAuth2ClientUser {

    /**
     * @see com.iteaj.framework.spi.auth.AuthType
     * @return
     */
    AuthType getType();

    String getAccount();

    String getOpenid();

    String getNickname();

    String getAvatarUrl();

    /**
     * sys、vip
     * @return
     */
    String getUserType();
}
