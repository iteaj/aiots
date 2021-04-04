package com.iteaj.security.shiro.online;

import com.iteaj.framework.spi.OnlineSession;
import org.apache.shiro.session.mgt.SimpleSession;

import java.io.Serializable;

/**
 * create time: 2021/3/20
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroOnlineSession extends SimpleSession implements OnlineSession {

    private String action;
    private String account;

    @Override
    public String getAccount() {
        return this.account;
    }

    @Override
    public String getAction() {
        return this.action;
    }

    @Override
    public Serializable getSessionId() {
        return this.getId();
    }

    public ShiroOnlineSession setAction(String action) {
        this.action = action;
        return this;
    }

    public ShiroOnlineSession setAccount(String account) {
        this.account = account;
        return this;
    }
}
