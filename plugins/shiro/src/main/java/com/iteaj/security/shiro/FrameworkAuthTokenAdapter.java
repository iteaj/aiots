package com.iteaj.security.shiro;

import com.iteaj.framework.spi.auth.AuthToken;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class FrameworkAuthTokenAdapter implements AuthenticationToken {

    private AuthToken authToken;

    public FrameworkAuthTokenAdapter(AuthToken authToken) {
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

    public FrameworkAuthTokenAdapter setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
        return this;
    }
}
