package com.iteaj.framework.spi.admin.auth;

import java.io.Serializable;

public interface AuthenticationUser {

    /**
     * 是否允许登录
     * @return 是=true, 否=false
     */
    boolean allowLogin();

    /**
     * 获取用户ID
     */
    Serializable getId();

    /**
     * 获取用户名称
     */
    String getName();

    /**
     * 获取账号信息
     */
    String getAccount();

    /**
     * 获取密码
     */
    String getPassword();
}
