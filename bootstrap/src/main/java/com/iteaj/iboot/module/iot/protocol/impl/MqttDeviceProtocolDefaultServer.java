package com.iteaj.iboot.module.iot.protocol.impl;

import com.iteaj.iboot.module.iot.protocol.DeviceProtocol;

/**
 * Mqtt协议服务端默认实现
 */
public class MqttDeviceProtocolDefaultServer implements DeviceProtocol {

    @Override
    public String getCode() {
        return "MQTT:CLIENT:DEBUG";
    }

}
