package com.iteaj.security.shiro;

import com.iteaj.framework.spi.auth.AuthToken;
import org.apache.shiro.authc.UsernamePasswordToken;

import java.io.Serializable;

/**
 * create time: 2020/6/27
 *
 * @author iteaj
 * @since 1.0
 */
public class AutoLoginToken extends UsernamePasswordToken implements AuthToken {

    private Serializable sessionId;

    public AutoLoginToken(String username, Serializable sessionId) {
        super(username, new char[]{});
        this.sessionId = sessionId;
    }

    public Serializable getSessionId() {
        return sessionId;
    }

    public AutoLoginToken setSessionId(Serializable sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    @Override
    public Object getCaptcha() {
        throw new UnsupportedOperationException("自动登录不支持验证码校验");
    }
}
