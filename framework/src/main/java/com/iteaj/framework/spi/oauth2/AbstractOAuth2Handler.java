package com.iteaj.framework.spi.oauth2;

import com.iteaj.framework.spi.auth.AccountAuthHandler;
import com.iteaj.framework.spi.app.AppAuthAction;

import java.util.List;

/**
 * create time: 2021/3/25
 *  Oauth2授权处理器
 * @author iteaj
 * @since 1.0
 */
public abstract class AbstractOAuth2Handler extends AccountAuthHandler {

    public AbstractOAuth2Handler(List<AppAuthAction> actions) {
        super(actions);
    }

    /**
     * 刷新access token （续期）
     *
     */
    protected abstract OAuth2Token refresh(String clientId);
}
