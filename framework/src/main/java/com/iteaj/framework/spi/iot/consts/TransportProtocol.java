package com.iteaj.framework.spi.iot.consts;

import com.iteaj.framework.IVOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 传输协议
 */
public enum TransportProtocol {

    TCP("TCP协议"),
    UDP("UDP协议"),
    MQTT("MQTT协议"),
    HTTP("HTTP协议"),
    SERIAL("SERIAL协议"),
    WEBSOCKET("WEBSOCKET协议"),
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
