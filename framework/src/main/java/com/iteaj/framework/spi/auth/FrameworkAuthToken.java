package com.iteaj.framework.spi.auth;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class FrameworkAuthToken implements AuthToken{



    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
