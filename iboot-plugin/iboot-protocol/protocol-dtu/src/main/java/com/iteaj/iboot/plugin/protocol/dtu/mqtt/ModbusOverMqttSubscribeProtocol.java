package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttProtocolType;
import com.iteaj.iot.client.protocol.ServerInitiativeProtocol;
import io.netty.handler.codec.mqtt.MqttQoS;

public class ModbusOverMqttSubscribeProtocol extends ServerInitiativeProtocol<ModbusOverMqttMessage> {

    private String topic;
    private MqttQoS qoS;

    public ModbusOverMqttSubscribeProtocol(ModbusOverMqttMessage requestMessage) {
        super(requestMessage);
    }

    @Override
    protected void doBuildRequestMessage(ModbusOverMqttMessage requestMessage) {
        this.qoS = requestMessage.getQos();
        this.topic = requestMessage.getTopic();
    }

    @Override
    protected ModbusOverMqttMessage doBuildResponseMessage() {
        return null;
    }

    @Override
    public ProtocolType protocolType() {
        return DefaultMqttProtocolType.Subscribe;
    }

    public String getTopic() {
        return topic;
    }

    public MqttQoS getQoS() {
        return qoS;
    }
}
