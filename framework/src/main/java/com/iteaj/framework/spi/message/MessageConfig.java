package com.iteaj.framework.spi.message;

import cn.hutool.json.JSONObject;

public interface MessageConfig {

    /**
     * 获取服务类型
     * @see MessageService#getType()
     * @return
     */
    String getType();

    /**
     * 获取通道标识
     * @return
     */
    String getChannelId();

    /**
     * 额外配置
     * @return
     */
    JSONObject getConfig();

}
