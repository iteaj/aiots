package com.iteaj.framework.observer.event;

import com.iteaj.framework.authority.ModuleResource;
import com.iteaj.framework.consts.MenuEventType;
import com.iteaj.framework.observer.Event;

public class AuthorityEvent implements Event {

    private MenuEventType type;
    private ModuleResource menu;

    public AuthorityEvent(MenuEventType type, ModuleResource menu) {
        this.type = type;
        this.menu = menu;
    }

    public AuthorityEvent(ModuleResource menu) {
        this.menu = menu;
    }

    public MenuEventType getType() {
        return type;
    }

    public void setType(MenuEventType type) {
        this.type = type;
    }

    public ModuleResource getMenu() {
        return menu;
    }

    public void setMenu(ModuleResource menu) {
        this.menu = menu;
    }
}
