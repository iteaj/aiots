package com.iteaj.framework.security.shiro;

import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;

@Data
public class ShiroAdminToken extends UsernamePasswordToken {

    /**
     * 验证码
     */
    private String captcha;
}
