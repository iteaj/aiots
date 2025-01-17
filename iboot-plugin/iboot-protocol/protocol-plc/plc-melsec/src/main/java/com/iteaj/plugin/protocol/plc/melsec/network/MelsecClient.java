package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDoubleBase;

public class MelsecClient extends PlcClientAdapter {

    public MelsecClient(PlcClientComponent clientComponent, MelsecConnectProperties connectProperties) {
        super(clientComponent, connectProperties);
    }

    @Override
    protected NetworkDoubleBase doInit(PlcConnectProperties config) {
        MelsecConnectProperties melsec = (MelsecConnectProperties) config;
        if(melsec.getProtocolType() == MelsecProtocolType.A1E) {
            return new MelsecA1ENet(config.getHost(), config.getPort());
        } else {
            return new MelsecMcNet(config.getHost(), config.getPort());
        }
    }
}
