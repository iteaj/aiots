package com.iteaj.framework.shiro;

import org.apache.shiro.authc.AuthenticationException;

public class CommonAuthenticationException extends AuthenticationException {
    public CommonAuthenticationException() {
        super();
    }

    public CommonAuthenticationException(String message) {
        super(message);
    }

    public CommonAuthenticationException(Throwable cause) {
        super(cause);
    }

    public CommonAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
