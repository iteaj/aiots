package com.iteaj.security.shiro;

import com.iteaj.framework.spi.admin.auth.AuthToken;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * create time: 2020/6/27
 *
 * @author iteaj
 * @since 1.0
 */
public class AutoLoginToken extends UsernamePasswordToken implements AuthToken {

    public AutoLoginToken(String username) {
        super(username, new char[]{});
    }

    @Override
    public Object getCaptcha() {
        throw new UnsupportedOperationException("自动登录不支持验证码校验");
    }
}
