package com.iteaj.framework.spi.iot;

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.consts.ConnectionType;
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
     * 连接类型
     * @return
     */
    ConnectionType getConnectionType();

    /**
     * 实现方式
     * @return
     */
    ProtocolImplMode getImplMode();

    /**
     * 产品额外配置
     * @return
     */
    default ParamMeta[] getProductConfig() {
        return new ParamMeta[]{};
    }

    /**
     * 设备额外配置
     * @return
     */
    default ParamMeta[] getDeviceConfig() {
        return new ParamMeta[]{};
    }

    /**
     * 网关额外配置
     * @return
     */
    default ParamMeta[] getGatewayConfig() {
        return new ParamMeta[]{};
    }

    /**
     * 获取已经创建的组件
     * @return
     */
    T getComponent();

    /**
     * 创建组件
     * @param config 服务端组件必填
     * @return 如果已经存在直接返回, 不存在则创建
     */
    T createComponent(C config);
}
