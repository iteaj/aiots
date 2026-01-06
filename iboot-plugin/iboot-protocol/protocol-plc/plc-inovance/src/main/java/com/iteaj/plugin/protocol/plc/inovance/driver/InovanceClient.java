package com.iteaj.plugin.protocol.plc.inovance.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDoubleBase;
import com.iteaj.plugin.protocol.plc.inovance.profinet.InovanceTcpNet;

public class InovanceClient extends PlcClientAdapter {

    public InovanceClient(PlcClientComponent clientComponent, InovanceConnectProperties connectProperties) {
        super(clientComponent, connectProperties);
    }

    @Override
    protected NetworkDoubleBase doInit(PlcConnectProperties config) {
        InovanceConnectProperties properties = (InovanceConnectProperties) config;
        return new InovanceTcpNet(properties.getSeries(), config.getHost(), config.getPort(), properties.getStation());
    }
}
