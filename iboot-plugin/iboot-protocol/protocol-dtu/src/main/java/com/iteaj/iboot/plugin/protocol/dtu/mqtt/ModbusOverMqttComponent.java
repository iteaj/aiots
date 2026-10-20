package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.mqtt.MqttClient;
import com.iteaj.iot.client.mqtt.MqttClientComponent;
import com.iteaj.iot.client.mqtt.MqttConnectProperties;
import com.iteaj.iot.client.protocol.ServerInitiativeProtocol;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;

import java.util.List;
import java.util.Objects;

public class ModbusOverMqttComponent extends MqttClientComponent<ModbusOverMqttMessage> {

    private static final String NAME = "MQTT<ModbusOver>";
    private static final String DESC = "Modbus Over Mqtt客户端实现";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDesc() {
        return DESC;
    }

    @Override
    protected ServerInitiativeProtocol doGetProtocol(ModbusOverMqttMessage message, ProtocolType type) {
        return new ModbusOVerMqttHeartProtocol(message);
    }

    @Override
    public Class<ModbusOverMqttMessage> getMessageClass() {
        return ModbusOverMqttMessage.class;
    }

    @Override
    public ModbusOverMqttMessage createMessage(byte[] message) {
        return new ModbusOverMqttMessage(message);
    }

    @Override
    protected List<MqttTopicSubscription> doSubscribe(MqttConnectProperties client) {
        ModbusOverMqttConnectProperties properties = (ModbusOverMqttConnectProperties) client;
        return properties.getTopics();
    }

    @Override
    public MqttClient getClient(Object clientId) {
        if(Objects.equals(clientId, ModbusOverMqttForDtuProtocolSupplier.VoidClient)) {
            return ModbusOverMqttVoidClient.getInstance();
        }

        return super.getClient(clientId);
    }

    @Override
    public void addClient(Object clientKey, IotClient value) {
        if(value != ModbusOverMqttVoidClient.getInstance()) {
            super.addClient(clientKey, value);
        }
    }
}
