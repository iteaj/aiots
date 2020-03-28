package com.iteaj.framework.observer.handle;

import com.iteaj.framework.api.AuthorityManager;
import com.iteaj.framework.observer.event.AuthorityEvent;

@FunctionalInterface
public interface AuthorityEventHandle {

    /**
     * 菜单事件处理
     * @param event
     */
    void handle(AuthorityEvent event, AuthorityManager manager);
}
