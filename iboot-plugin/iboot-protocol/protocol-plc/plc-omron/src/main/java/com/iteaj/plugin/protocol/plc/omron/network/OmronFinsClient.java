package com.iteaj.plugin.protocol.plc.omron.network;

import com.iteaj.iot.client.ClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDoubleBase;

public class OmronFinsClient extends PlcClientAdapter {

    public OmronFinsClient(PlcClientComponent clientComponent, PlcConnectProperties connectProperties) {
        super(clientComponent, connectProperties);
    }

    @Override
    protected NetworkDoubleBase doInit(PlcConnectProperties config) {
        return new OmronFinsNet(config.getHost(), config.getPort());
    }
}
