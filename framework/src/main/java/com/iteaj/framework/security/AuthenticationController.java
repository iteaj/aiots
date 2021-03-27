package com.iteaj.framework.security;

import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.util.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * create time: 2021/3/14
 *
 * @author iteaj
 * @since 1.0
 */
public interface AuthenticationController<T extends AuthToken> {

    Result<AuthzUrl> login(T token);

    Result<AuthzUrl> logout(HttpServletRequest request, HttpServletResponse response);
}
