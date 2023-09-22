package com.iteaj.iboot.module.iot.common.spi;

import com.iteaj.iboot.module.iot.common.protocol.ProtocolModel;
import com.iteaj.iboot.module.iot.consts.ProtocolImplMode;
import com.iteaj.iboot.module.iot.entity.Gateway;
import com.iteaj.iot.FrameworkComponent;

/**
 * 协议提供
 */
public interface DeviceProtocolSupplier {

    /**
     * 协议描述
     * @return
     */
    String getDesc();

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
     * @return
     */
    FrameworkComponent createComponent(Gateway config);
}
