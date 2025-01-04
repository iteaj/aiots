package com.iteaj.plugin.protocol.plc.panasonic.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDoubleBase;
import com.iteaj.plugin.protocol.plc.panasonic.profinet.PanasonicMewtocolOverTcp;

public class PanasonicClient extends PlcClientAdapter {

    public PanasonicClient(PlcClientComponent clientComponent, PanasonicConnectProperties connectProperties) {
        super(clientComponent, connectProperties);
    }

    @Override
    protected NetworkDoubleBase doInit(PlcConnectProperties config) {
        PanasonicConnectProperties properties = (PanasonicConnectProperties) config;
        return new PanasonicMewtocolOverTcp(config.getHost(), config.getPort(), properties.getStation());
    }
}
