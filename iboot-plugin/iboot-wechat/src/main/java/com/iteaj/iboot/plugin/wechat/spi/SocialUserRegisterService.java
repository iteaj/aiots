package com.iteaj.iboot.plugin.wechat.spi;

import com.iteaj.iboot.plugin.wechat.entity.SocialUser;

/**
 * 社交用户注册SPI：由接入层（bootstrap）实现，用于创建或关联系统用户
 * @author iteaj
 */
public interface SocialUserRegisterService {

    /**
     * 为微信用户注册或获取对应的系统用户ID
     * @param socialUser 社交用户（已持久化）
     * @return 系统用户ID
     */
    Long registerOrGet(SocialUser socialUser);
}
