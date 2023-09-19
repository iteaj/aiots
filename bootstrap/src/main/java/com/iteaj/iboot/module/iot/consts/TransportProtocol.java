package com.iteaj.iboot.module.iot.consts;

import com.iteaj.framework.IVOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 传输协议
 */
public enum TransportProtocol {

    TCP("tcp协议"),
    UDP("udp协议"),
    MQTT("mqtt协议"),
    SERIAL("串口协议"),
    WEBSOCKET("websocket协议"),
    ;

    private String desc;

    TransportProtocol(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static List<IVOption> options() {
        return Arrays.stream(TransportProtocol.values())
                .map(item -> new IVOption(item.getDesc(), item.name()))
                .collect(Collectors.toList());
    }
}
