package com.iteaj.msn.core.auth;

import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;

@Data
public class AdminAuthToken extends UsernamePasswordToken {

    /**
     * 验证码
     */
    private String captcha;
}
