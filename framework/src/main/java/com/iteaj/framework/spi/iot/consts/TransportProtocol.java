package com.iteaj.framework.spi.iot.consts;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 传输协议
 */
public enum TransportProtocol {

    TCP("TCP协议", NetworkConfigImpl.TcpNetworkConfig.tcpMetas),
    UDP("UDP协议", NetworkConfigImpl.UdpNetworkConfig.udpMetas),
    MQTT("MQTT协议", NetworkConfigImpl.MqttNetworkConfig.mqttMetas),
    HTTP("HTTP协议", NetworkConfigImpl.HttpNetworkConfig.httpMetas),
    SERIAL("SERIAL协议", NetworkConfigImpl.SerialConfig.serialMetas),
    WEBSOCKET("WEBSOCKET协议", NetworkConfigImpl.WebSocketConfig.websocketMetas),
    ;

    private String desc;
    private ParamMeta[] metas;

    TransportProtocol(String desc, ParamMeta[] metas) {
        this.desc = desc;
        this.metas = metas;
    }

    public String getDesc() {
        return desc;
    }

    public ParamMeta[] getMetas() {
        return metas;
    }

    public static List<IVOption> options() {
        return Arrays.stream(TransportProtocol.values())
                .map(item -> new IVOption(item.getDesc(), item.name(), item.getMetas()))
                .collect(Collectors.toList());
    }
}
