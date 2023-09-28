package com.iteaj.iboot.module.iot.protocol.mqtt;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttComponent;

/**
 * 用于提供mqtt协议调试
 */
public class MqttDebugProtocolSupplier implements StaticProtocolSupplier<NetworkConfigImpl.MqttNetworkConfig> {

    private ProtocolModel mqttDebugProtocolModel = new DefaultProtocolModel("MQTT:CLIENT:DEBUG"
            , CtrlMode.COMMON, TransportProtocol.MQTT)
            .addModelApi(new MqttSubscribeDebugProtocolModelApi())
            .addModelApi(new MqttPublishDebugProtocolModelApi());

    @Override
    public String getDesc() {
        return "用于做Mqtt协议调试";
    }

    @Override
    public ProtocolModel getProtocol() {
        return mqttDebugProtocolModel;
    }

    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }

    @Override
    public FrameworkComponent createComponent(NetworkConfigImpl.MqttNetworkConfig config) {
        return new DefaultMqttComponent();
    }
}
