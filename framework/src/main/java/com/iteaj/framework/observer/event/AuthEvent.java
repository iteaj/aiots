package com.iteaj.framework.observer.event;

import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.consts.AuthEventType;
import com.iteaj.framework.observer.Event;

/**
 * 系统认证事件
 */
public class AuthEvent implements Event {

    private BaseEntity admin;
    private AuthEventType type;

    public AuthEvent(BaseEntity admin, AuthEventType type) {
        this.admin = admin;
        this.type = type;
    }

    public AuthEventType getType() {
        return type;
    }

    public void setType(AuthEventType type) {
        this.type = type;
    }

    public BaseEntity getAdmin() {
        return admin;
    }

    public void setAdmin(BaseEntity admin) {
        this.admin = admin;
    }
}
