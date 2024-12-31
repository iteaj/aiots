package com.iteaj.plugin.protocol.plc.omron.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolAbstract;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

public class OmronProtocol extends PlcProtocolAbstract<OmronMessage> {

    public OmronProtocol(PlcClientAdapter client) {
        super(PlcProtocolType.Omron, client);
    }

    @Override
    public OmronMessage build(byte[] message) {
        return new OmronMessage(message);
    }
}
