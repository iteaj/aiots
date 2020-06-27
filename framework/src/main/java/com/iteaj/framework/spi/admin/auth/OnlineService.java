package com.iteaj.framework.spi.admin.auth;

import com.iteaj.framework.spi.OnlineSession;

import java.io.Serializable;

/**
 * create time: 2020/6/25
 *
 * @author iteaj
 * @since 1.0
 */
public interface OnlineService {

    OnlineSession getOnlineSession(Serializable sessionId);
}
