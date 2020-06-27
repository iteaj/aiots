package com.iteaj.framework.spi;

import java.io.Serializable;
import java.util.Date;

/**
 * create time: 2020/6/25
 *  在线会话
 * @author iteaj
 * @since 1.0
 */
public interface OnlineSession {

    /**
     * 当前会话的账号
     * @return
     */
    String getAccount();

    Serializable getSessionId();

    long getTimeout();

    Date getStartTimestamp();

    Date getLastAccessTime();
}
