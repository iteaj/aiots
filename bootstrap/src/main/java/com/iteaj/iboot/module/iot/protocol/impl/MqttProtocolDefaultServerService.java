package com.iteaj.iboot.module.iot.protocol.impl;

import com.iteaj.iboot.module.iot.protocol.ProtocolService;

/**
 * Mqtt协议服务端默认实现
 */
public class MqttProtocolDefaultServerService implements ProtocolService {

    @Override
    public String getCode() {
        return "MQTT:CLIENT:DEBUG";
    }

}
