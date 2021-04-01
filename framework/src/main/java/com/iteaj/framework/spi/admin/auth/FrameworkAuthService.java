package com.iteaj.framework.spi.admin.auth;

import com.iteaj.framework.spi.auth.AuthToken;
import com.iteaj.util.module.mvc.orm.Entity;

import java.io.Serializable;

/**
 * create time: 2020/6/20
 *
 * @author iteaj
 * @since 1.0
 */
public interface FrameworkAuthService<T extends AuthToken>{

    /**
     *  是否是当前的同一个会话
     * @param sessionId
     * @return
     */
    boolean isCurrent(Serializable sessionId);

    /**
     * 此方法返回认证的对象
     * @return 只有登录之后才会返回值
     */
    Entity getUser();

}
