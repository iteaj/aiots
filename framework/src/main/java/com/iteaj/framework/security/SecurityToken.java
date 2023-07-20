package com.iteaj.framework.security;

import lombok.Data;

@Data
public class SecurityToken {

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    private boolean rememberMe = false;
}
