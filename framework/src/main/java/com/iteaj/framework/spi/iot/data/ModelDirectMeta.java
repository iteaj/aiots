package com.iteaj.framework.spi.iot.data;

import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;

/**
 * 模型指令元数据
 */
public interface ModelDirectMeta {

    /**
     * 模型api接口码
     * @return 接口码
     */
    String getCode();

    /**
     * 模型api接口名称
     * @return 接口名称
     */
    String getApiName();

    /**
     * 指令备注
     * @return 备注
     */
    String getRemark();

    /**
     * 指令码名称
     * @return 指令码名称
     */
    String getCommandName();

    /**
     * 指令码
     * @return 指令码
     */
    String getCommandValue();

    /**
     * 模型api接口类型
     * @return 模型api接口类型
     */
    ProtocolApiType getApiType();
}
