package com.iteaj.plugin.protocol.plc.inovance.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolAbstract;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

public class InovanceProtocol extends PlcProtocolAbstract<InovanceMessage> {

    public InovanceProtocol(PlcClientAdapter client) {
        super(PlcProtocolType.Inovance, client);
    }

    @Override
    public InovanceMessage build(byte[] message) {
        return new InovanceMessage(message);
    }
}
