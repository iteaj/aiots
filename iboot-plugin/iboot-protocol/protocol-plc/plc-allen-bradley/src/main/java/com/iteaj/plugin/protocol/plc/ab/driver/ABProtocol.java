package com.iteaj.plugin.protocol.plc.ab.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolAbstract;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

public class ABProtocol extends PlcProtocolAbstract<ABMessage> {

    public ABProtocol(PlcClientAdapter client) {
        super(PlcProtocolType.AB, client);
    }

    @Override
    public ABMessage build(byte[] message) {
        return new ABMessage(message);
    }
}
