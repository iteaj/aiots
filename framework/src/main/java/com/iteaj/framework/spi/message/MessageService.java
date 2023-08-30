package com.iteaj.framework.spi.message;

import com.iteaj.framework.ParamMeta;

import java.util.List;

public interface MessageService<C extends MessageConfig> {

    String DEFAULT_CHANNEL = "DEFAULT";

    String DEFAULT_CHANNEL_NAME = "默认";

    /**
     * 消息类型
     * @return
     */
    String getType();

    /**
     * 消息名称
     * @return
     */
    String getName();

    /**
     * 获取通道标识
     * @return
     */
    default String getChannelId() {
        return DEFAULT_CHANNEL;
    }

    /**
     * 获取通道名称
     * @return
     */
    default String getChannelName() {
        return DEFAULT_CHANNEL_NAME;
    }

    /**
     * 返回配置参数
     * @see MessageConfig#getConfig()
     * @return
     */
    List<ParamMeta> getConfigParams();

    /**
     * 发送参数
     * @param config
     * @param args
     */
    void send(C config, Object args);
}
