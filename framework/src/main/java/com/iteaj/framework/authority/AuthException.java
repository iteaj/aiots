package com.iteaj.framework.authority;

/**
 * create time: 2019/12/6
 *
 * @author iteaj
 * @since 1.0
 */
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
