package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.iot.client.mqtt.impl.DefaultMqttProtocolType;
import com.iteaj.iot.client.mqtt.message.MqttClientMessage;
import com.iteaj.iot.client.mqtt.message.MqttMessageHead;
import io.netty.handler.codec.mqtt.MqttQoS;

public class ModbusOverMqttMessage extends MqttClientMessage {

    public ModbusOverMqttMessage(byte[] message) {
        super(message);
    }

    public ModbusOverMqttMessage(MqttMessageHead head, String topic) {
        super(head, topic);
    }

    public ModbusOverMqttMessage(MqttMessageHead head, MqttQoS qos, String topic) {
        super(head, qos, topic);
    }

    public ModbusOverMqttMessage(MqttMessageHead head, MessageBody body, String topic) {
        super(head, body, topic);
    }

    public ModbusOverMqttMessage(MqttMessageHead head, MessageBody body, MqttQoS qos, String topic) {
        super(head, body, qos, topic);
    }

    @Override
    protected MqttMessageHead doBuild(byte[] payload) {
        String deviceSn = getProperties().connectKey();
        String messageId = String.format("%s:%d:%d", deviceSn, payload[0], payload[1]);
        return new MqttMessageHead(deviceSn, messageId, DefaultMqttProtocolType.Subscribe);
    }
}
