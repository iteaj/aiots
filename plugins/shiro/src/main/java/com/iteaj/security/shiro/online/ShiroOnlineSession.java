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

    private String account;


    @Override
    public String getAccount() {
        return this.account;
    }

    @Override
    public Serializable getSessionId() {
        return this.getId();
    }

    public ShiroOnlineSession setAccount(String account) {
        this.account = account;
        return this;
    }
}
