package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;

public class MelsecConnectProperties extends PlcConnectProperties {

    /**
     * 协议类型
     */
    private MelsecProtocolType protocolType;

    public MelsecConnectProperties(String remoteHost, Integer remotePort, MelsecProtocolType protocolType) {
        super(remoteHost, remotePort);
        this.protocolType = protocolType;
    }

    public MelsecProtocolType getProtocolType() {
        return protocolType;
    }
}
