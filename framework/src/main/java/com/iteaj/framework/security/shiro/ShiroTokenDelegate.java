package com.iteaj.framework.security.shiro;

import com.iteaj.framework.spi.auth.AuthToken;
import org.apache.shiro.authc.AuthenticationToken;

public class ShiroTokenDelegate implements AuthenticationToken {

    private AuthToken authToken;

    public ShiroTokenDelegate(AuthToken authToken) {
        this.authToken = authToken;
    }

    @Override
    public Object getPrincipal() {
        return this.authToken.getPrincipal();
    }

    @Override
    public Object getCredentials() {
        return this.authToken.getCredentials();
    }

    public AuthToken getAuthToken() {
        return authToken;
    }
}
