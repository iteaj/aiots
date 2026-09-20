package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.spi.auth.AuthType;

public interface OAuth2ClientService {

    OAuth2ClientUser getUser(String openid, AuthType type);

    OAuth2ClientUser getAndCreateOAuth2ClientUser(Oauth2Result user);
}
