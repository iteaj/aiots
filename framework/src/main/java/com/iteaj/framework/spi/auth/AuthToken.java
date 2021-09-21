package com.iteaj.framework.spi.auth;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

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

    /**
     * 角色列表
     * @return
     */
    default Collection<String> getRoles() {return Collections.emptyList();}

    /**
     * 权限列表
     * @return
     */
    default Collection<String> getPermissions(){return Collections.emptyList();}
}
