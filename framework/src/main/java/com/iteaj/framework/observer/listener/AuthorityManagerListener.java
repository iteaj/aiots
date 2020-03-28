package com.iteaj.framework.observer.listener;

import com.iteaj.framework.api.AuthorityManager;
import com.iteaj.framework.authority.ModuleResource;
import com.iteaj.framework.observer.Listener;
import com.iteaj.framework.observer.event.AuthEvent;
import com.iteaj.framework.observer.event.AuthorityEvent;
import com.iteaj.framework.observer.handle.AuthEventHandle;
import com.iteaj.framework.observer.handle.AuthorityEventHandle;
import org.apache.shiro.event.Subscribe;

import java.util.Collection;

/**
 * 菜单管理事件
 * @see AuthorityEvent 菜单事件
 * @see com.iteaj.framework.consts.MenuEventType 事件类型包括(注册, 移除, 修改)
 * @see AuthorityManager#register(ModuleResource) 发布注册事件
 */
public class AuthorityManagerListener implements Listener {

    private Collection<AuthEventHandle> authEventHandles;
    private AuthorityManager authorityManager;
    private Collection<AuthorityEventHandle> authorityEventHandles;

    public AuthorityManagerListener(AuthorityManager authorityManager
            , Collection<AuthEventHandle> authEventHandles
            , Collection<AuthorityEventHandle> authorityEventHandles) {
        this.authEventHandles = authEventHandles;
        this.authorityEventHandles = authorityEventHandles;
        this.authorityManager = authorityManager;
    }

    /**
     * 权限管理事件
     * @param event
     */
    @Subscribe
    public void authorityEventListener(AuthorityEvent event) {
        this.authorityEventHandles.stream().forEach((handle)->{
            handle.handle(event, this.authorityManager);
        });
    }

    /**
     * 认证事件
     * @param event
     */
    @Subscribe
    public void authEventListener(AuthEvent event) {
        this.authEventHandles.stream().forEach((handle)->{
            handle.handle(event, this.authorityManager);
        });
    }

    public AuthorityManager getAuthorityManager() {
        return authorityManager;
    }

    public void setAuthorityManager(AuthorityManager authorityManager) {
        this.authorityManager = authorityManager;
    }
}
