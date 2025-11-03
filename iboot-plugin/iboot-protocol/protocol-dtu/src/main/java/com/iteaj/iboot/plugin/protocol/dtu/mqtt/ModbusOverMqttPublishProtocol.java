package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.SocketClient;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttProtocolType;
import com.iteaj.iot.client.mqtt.message.MqttMessageHead;
import com.iteaj.iot.client.protocol.ClientInitiativeProtocol;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.utils.ByteUtil;
import io.netty.handler.codec.mqtt.MqttQoS;

public class ModbusOverMqttPublishProtocol extends ClientInitiativeProtocol<ModbusOverMqttMessage> {

    private final byte[] msg;

    private final MqttQoS qoS;

    private final String topic;

    private final IotClient client;

    public ModbusOverMqttPublishProtocol(byte[] msg, String topic, IotClient client) {
        this(msg, MqttQoS.AT_MOST_ONCE, topic, client);
    }

    public ModbusOverMqttPublishProtocol(byte[] msg, MqttQoS qoS, String topic, IotClient client) {
        this.msg = msg;
        this.qoS = qoS;
        this.topic = topic;
        this.client = client;
    }

    @Override
    protected ModbusOverMqttMessage doBuildRequestMessage() {
        String deviceSn = client.getConfig().connectKey();
        byte code = msg[1];
        String messageId;
        if(code >= ModbusCode.Write05.getCode()) {
            short address = ByteUtil.bytesToShort(msg, 2);
            messageId = String.format("%s:%d:%d:%d", deviceSn, msg[0], code, address);
        } else {
            messageId = String.format("%s:%d:%d", deviceSn, this.msg[0], code);
        }

        MqttMessageHead messageHead = new MqttMessageHead(deviceSn, messageId, protocolType());
        messageHead.setMessage(this.msg);
        return new ModbusOverMqttMessage(messageHead, this.qoS, this.topic);
    }

    @Override
    public void doBuildResponseMessage(ModbusOverMqttMessage responseMessage) {

    }

    @Override
    public ModbusOverMqttPublishProtocol timeout(long timeout) {
        return (ModbusOverMqttPublishProtocol) super.timeout(timeout);
    }

    @Override
    public ProtocolType protocolType() {
        return DefaultMqttProtocolType.Publish;
    }

    @Override
    public SocketClient getIotClient() {
        return (SocketClient) client;
    }
}
