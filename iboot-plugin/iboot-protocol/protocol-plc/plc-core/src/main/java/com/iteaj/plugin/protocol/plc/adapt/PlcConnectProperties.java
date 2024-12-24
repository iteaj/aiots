package com.iteaj.plugin.protocol.plc.adapt;

import com.iteaj.iot.client.ClientConnectProperties;

public class PlcConnectProperties extends ClientConnectProperties {

    public PlcConnectProperties() { }

    public PlcConnectProperties(Integer remotePort) {
        super(remotePort);
    }

    public PlcConnectProperties(String remoteHost, Integer remotePort) {
        super(remoteHost, remotePort);
    }

    public PlcConnectProperties(String remoteHost, Integer remotePort, String connectKey) {
        super(remoteHost, remotePort, connectKey);
    }

    public PlcConnectProperties(String remoteHost, Integer remotePort, Integer localPort) {
        super(remoteHost, remotePort, localPort);
    }

    public PlcConnectProperties(String remoteHost, Integer remotePort, String localHost, Integer localPort) {
        super(remoteHost, remotePort, localHost, localPort);
    }

    public PlcConnectProperties(String remoteHost, Integer remotePort, String localHost, Integer localPort, String connectKey) {
        super(remoteHost, remotePort, localHost, localPort, connectKey);
    }
}
