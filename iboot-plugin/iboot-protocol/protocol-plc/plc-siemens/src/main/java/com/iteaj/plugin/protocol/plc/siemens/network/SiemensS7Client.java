package com.iteaj.plugin.protocol.plc.siemens.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDoubleBase;

public class SiemensS7Client extends PlcClientAdapter {

    public SiemensS7Client(PlcClientComponent clientComponent, SiemensConnectProperties connectProperties) {
        super(clientComponent, connectProperties);
    }

    @Override
    protected NetworkDoubleBase doInit(PlcConnectProperties config) {
        SiemensConnectProperties siemens = (SiemensConnectProperties) config;
        return new SiemensS7Net(siemens.getModel(), siemens.getHost(), siemens.getPort());
    }
}
