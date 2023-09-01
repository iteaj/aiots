package com.iteaj.framework.spi.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteaj.framework.ParamMeta;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    String DEFAULT_CHANNEL = "DEFAULT";

    String DEFAULT_CHANNEL_NAME = "默认";

    ObjectMapper mapper = new ObjectMapper();

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
     * @param model
     */
    Optional<Object> send(MessageConfig config, SendModel model);

    /**
     * 移除消息配置
     * @param config
     */
    boolean remove(MessageConfig config);
}
