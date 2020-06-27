package com.iteaj.framework.spi.admin.auth;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.security.AuthzUrl;
import com.iteaj.util.Result;
import com.iteaj.util.module.mvc.orm.Entity;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * create time: 2020/6/20
 *
 * @author iteaj
 * @since 1.0
 */
public interface AuthenticatingService<T extends AuthToken>{

    AuthzUrl getAuthzUrl();

    /**
     * 注销指定sessionId的用户
     * @param sessionId
     */
    void logout(Serializable sessionId);

    /**
     *  是否是当前的同一个会话
     * @param sessionId
     * @return
     */
    boolean equalToCurrent(Serializable sessionId);

    /**
     * 此方法返回认证的对象
     * @return 只有登录之后才会返回值
     */
    BaseEntity getPrincipal();
}
