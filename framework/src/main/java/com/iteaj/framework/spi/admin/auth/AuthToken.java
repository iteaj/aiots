package com.iteaj.framework.spi.admin.auth;

import java.io.Serializable;

/**
 * create time: 2019/12/6
 *
 * @author iteaj
 * @since 1.0
 */
public interface AuthToken extends Serializable {

    Object getCaptcha();

    /**
     * 帐号
     * @return
     */
    Object getPrincipal();

    /**
     * 安全相关认证 e.g: 密码
     * @return
     */
    Object getCredentials();
}
