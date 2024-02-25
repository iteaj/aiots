package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iot.FrameworkComponent;

/**
 * 协议提供
 */
public interface DeviceProtocolSupplier<C extends NetworkConfig, T extends FrameworkComponent> {

    /**
     * 协议描述
     * @return
     */
    String getDesc();

    /**
     * 协议版本
     * @return
     */
    String getVersion();

    /**
     * 返回协议配置
     * @return
     */
    ProtocolModel getProtocol();

    /**
     * 实现方式
     * @return
     */
    ProtocolImplMode getImplMode();

    /**
     * 创建组件
     * @param config
     * @return 如果已经存在直接返回, 不存在则创建
     */
    T createComponent(C config);
}
