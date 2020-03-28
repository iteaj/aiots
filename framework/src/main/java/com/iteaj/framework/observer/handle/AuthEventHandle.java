package com.iteaj.framework.observer.handle;

import com.iteaj.framework.api.AuthorityManager;
import com.iteaj.framework.observer.event.AuthEvent;

@FunctionalInterface
public interface AuthEventHandle {

    /**
     * 认证事件处理
     * @param event
     */
    void handle(AuthEvent event, AuthorityManager manager);
}
