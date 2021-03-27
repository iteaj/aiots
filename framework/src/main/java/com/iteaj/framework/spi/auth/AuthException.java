package com.iteaj.framework.spi.auth;

import com.iteaj.framework.FrameworkException;

/**
 * create time: 2021/3/27
 *
 * @author iteaj
 * @since 1.0
 */
public class AuthException extends FrameworkException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
