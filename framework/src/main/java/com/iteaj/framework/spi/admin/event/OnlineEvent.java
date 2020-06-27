package com.iteaj.framework.spi.admin.event;

import com.iteaj.framework.spi.event.Event;
import com.iteaj.util.module.mvc.orm.Entity;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * create time: 2020/6/20
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineEvent extends Event {

    private Entity admin;
    private long timeout;
    private Date accessTime;
    private OnlineType type;
    private String accessIp;
    private UserAgent userAgent;

    public OnlineEvent(Object source, OnlineType type) {
        super(source);
        this.type = type;
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source {@link HttpServletRequest} or {@link String}
     * @throws IllegalArgumentException if source is null.
     */
    public OnlineEvent(Object source, UserAgent userAgent) {
        this(source, userAgent, new Date());
    }

    public OnlineEvent(Object source, UserAgent userAgent, Date accessTime) {
        super(source);
        this.userAgent = userAgent;
        this.accessTime = accessTime;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public OnlineEvent setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public OnlineEvent setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
        return this;
    }

    public Entity getAdmin() {
        return admin;
    }

    public OnlineEvent setAdmin(Entity admin) {
        this.admin = admin;
        return this;
    }

    public OnlineType getType() {
        return type;
    }

    public OnlineEvent setType(OnlineType type) {
        this.type = type;
        return this;
    }

    public String getAccessIp() {
        return accessIp;
    }

    public OnlineEvent setAccessIp(String accessIp) {
        this.accessIp = accessIp;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public OnlineEvent setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }
}
