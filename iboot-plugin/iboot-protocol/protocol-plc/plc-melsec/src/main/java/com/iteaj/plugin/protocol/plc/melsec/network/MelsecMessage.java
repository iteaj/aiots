package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientMessage;

public class MelsecMessage extends PlcClientMessage {

    public MelsecMessage(byte[] message) {
        super(message);
    }
}
