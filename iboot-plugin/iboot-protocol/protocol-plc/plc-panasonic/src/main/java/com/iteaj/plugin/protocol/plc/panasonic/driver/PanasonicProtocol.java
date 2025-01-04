package com.iteaj.plugin.protocol.plc.panasonic.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolAbstract;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

public class PanasonicProtocol extends PlcProtocolAbstract<PanasonicMessage> {

    public PanasonicProtocol(PlcClientAdapter client) {
        super(PlcProtocolType.Panasonic, client);
    }

    @Override
    public PanasonicMessage build(byte[] message) {
        return new PanasonicMessage(message);
    }
}
