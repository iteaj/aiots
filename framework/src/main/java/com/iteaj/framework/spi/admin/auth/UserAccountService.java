package com.iteaj.framework.spi.admin.auth;

import com.iteaj.framework.BaseEntity;
import com.iteaj.util.module.mvc.orm.Entity;

/**
 * create time: 2020/6/26
 *
 * @author iteaj
 * @since 1.0
 */
public interface UserAccountService<T extends AuthToken> {

    /**
     * 获取账号信息
     * @param token
     * @return
     */
    BaseEntity getAdmin(T token);

    /**
     * 密码校验
     * @param admin
     * @return
     */
    boolean validCredentials(Entity admin, T token);
}
