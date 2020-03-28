package com.iteaj.framework.authority;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.api.AuthorityManager;
import com.iteaj.framework.api.AuthorizingService;
import com.iteaj.framework.consts.AuthEventType;
import com.iteaj.framework.observer.event.AuthEvent;
import com.iteaj.framework.observer.handle.AuthEventHandle;
import com.iteaj.framework.spring.boot.AbstractWebConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @see com.iteaj.framework.shiro.UserPasswordRealm#triggerAuthEvent(BaseEntity, AuthEventType) 触发认证事件
 * 用来将各个模块的菜单列表注册到菜单管理{@link AuthorityManager}
 */
public class DefaultAuthEventHandle implements AuthEventHandle {

    private AuthorizingService authorizingService;
    private Collection<AbstractWebConfiguration> applications;
    private Logger logger = LoggerFactory.getLogger(DefaultAuthEventHandle.class);

    public DefaultAuthEventHandle(AuthorizingService authorizingService
            , Collection<AbstractWebConfiguration> applications) {
        this.authorizingService = authorizingService;
        this.applications = applications;
    }

    @Override
    public void handle(AuthEvent event, AuthorityManager manager) {
        // 注销事件不进行处理
        if(event.getType() == AuthEventType.LOGOUT) return;
    }
}
