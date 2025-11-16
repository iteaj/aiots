package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.iot.client.mqtt.MessageIdManager;
import com.iteaj.iot.client.mqtt.MqttClient;
import com.iteaj.iot.client.mqtt.MqttClientComponent;
import com.iteaj.iot.client.mqtt.MqttConnectProperties;
import io.netty.channel.ChannelFuture;

public class ModbusOverMqttVoidClient extends MqttClient {

    private static ModbusOverMqttVoidClient client;

    public ModbusOverMqttVoidClient(MqttClientComponent clientComponent, MqttConnectProperties config) {
        super(clientComponent, config);
    }

    public static ModbusOverMqttComponent build(ModbusOverMqttComponent component) {
        ModbusOverMqttConnectProperties properties = new ModbusOverMqttConnectProperties(""
                , 1883, "_ModbusOverMqttVoidClient", null);
        client = new ModbusOverMqttVoidClient(component, properties); return component;
    }

    public static ModbusOverMqttVoidClient getInstance() {
        return client;
    }

    @Override
    public ChannelFuture connect() {
        return null;
    }

    @Override
    public void init(Object config) {

    }
}
