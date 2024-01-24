package com.iteaj.iboot.module.iot.protocol.mqtt;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttComponent;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttMessage;

/**
 * 用于提供mqtt协议调试
 */
public class MqttDebugProtocolSupplier extends StaticProtocolSupplier<NetworkConfigImpl.MqttNetworkConfig> {

    private ProtocolModel mqttDebugProtocolModel = new DefaultProtocolModel("MQTT:CLIENT:DEBUG"
            , CtrlMode.COMMON, TransportProtocol.MQTT)
            .addModelApi(new MqttSubscribeDebugProtocolModelApi())
            .addModelApi(new MqttPublishDebugProtocolModelApi())
            .addModelAttr(new ProtocolModelAttr("msg", "报文","string", "发送的内容"))
            .addModelAttr(new ProtocolModelAttr("topic", "主题", "string", "发布/订阅的主题"))
            ;

    @Override
    public String getDesc() {
        return "用于做Mqtt协议调试";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
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
        FrameworkComponent component = FrameworkManager.getComponent(DefaultMqttMessage.class);
        if(component == null) {
            FrameworkManager.register(new DefaultMqttComponent()).start();
        }

        return component;
    }
}
