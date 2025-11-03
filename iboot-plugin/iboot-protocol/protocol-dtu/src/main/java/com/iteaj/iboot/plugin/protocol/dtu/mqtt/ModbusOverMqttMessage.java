package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.iot.client.mqtt.impl.DefaultMqttProtocolType;
import com.iteaj.iot.client.mqtt.message.MqttClientMessage;
import com.iteaj.iot.client.mqtt.message.MqttMessageHead;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.utils.ByteUtil;
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
        byte code = payload[1];
        String messageId;
        if(code >= ModbusCode.Write05.getCode()) { // 写操作
            short address = ByteUtil.bytesToShort(payload, 2);
            messageId = String.format("%s:%d:%d:%d", deviceSn, payload[0], code, address);
        } else {
            messageId = String.format("%s:%d:%d", deviceSn, payload[0], code);
        }
        return new MqttMessageHead(deviceSn, messageId, DefaultMqttProtocolType.Subscribe);
    }
}
