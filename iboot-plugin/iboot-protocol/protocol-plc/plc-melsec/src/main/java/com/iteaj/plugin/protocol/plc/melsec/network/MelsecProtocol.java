package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolAbstract;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

public class MelsecProtocol extends PlcProtocolAbstract<MelsecMessage> {

    public MelsecProtocol(PlcClientAdapter client) {
        super(PlcProtocolType.Melsec, client);
    }

    @Override
    public MelsecMessage build(byte[] message) {
        return new MelsecMessage(message);
    }

}
