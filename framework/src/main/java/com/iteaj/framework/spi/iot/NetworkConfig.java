package com.iteaj.framework.spi.iot;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.json.JacksonUtils;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;

import java.io.IOException;

public interface NetworkConfig {

    /**
     * 主机地址
     * @return
     */
    String getHost();

    /**
     * 端口
     * @return
     */
    Integer getPort();

    /**
     * 是否启用ssl
     * @return
     */
    Boolean isUseSsl();

    static NetworkConfig resolve(TransportProtocol protocol, ObjectNode config) {
        try {
            if(protocol == TransportProtocol.TCP) {
                return JacksonUtils.getMapper().readValue(config.traverse(), NetworkConfigImpl.TcpNetworkConfig.class);
            } else if(protocol == TransportProtocol.MQTT) {
                return JacksonUtils.getMapper().readValue(config.traverse(), NetworkConfigImpl.MqttNetworkConfig.class);
            } else if(protocol == TransportProtocol.UDP) {
                return JacksonUtils.getMapper().readValue(config.traverse(), NetworkConfigImpl.UdpNetworkConfig.class);
            } else if(protocol == TransportProtocol.HTTP) {
                return JacksonUtils.getMapper().readValue(config.traverse(), NetworkConfigImpl.HttpNetworkConfig.class);
            } else if(protocol == TransportProtocol.WEBSOCKET) {
                return JacksonUtils.getMapper().readValue(config.traverse(), NetworkConfigImpl.WebSocketConfig.class);
            } else if(protocol == TransportProtocol.SERIAL) {
                return JacksonUtils.getMapper().readValue(config.traverse(), NetworkConfigImpl.SerialConfig.class);
            } else {
                throw new ServiceException("错误的协议类型["+protocol+"]");
            }
        } catch (IOException e) {
            throw new ServiceException("解析配置失败", e);
        }
    }
}
