package com.iteaj.framework.authority;

import java.io.Serializable;

/**
 * create time: 2019/12/6
 *
 * @author iteaj
 * @since 1.0
 */
public interface AuthToken extends Serializable {

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
