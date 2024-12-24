package com.iteaj.plugin.protocol.plc.omron.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientMessage;

public class OmronMessage extends PlcClientMessage {

    public OmronMessage(byte[] message) {
        super(message);
    }
}
