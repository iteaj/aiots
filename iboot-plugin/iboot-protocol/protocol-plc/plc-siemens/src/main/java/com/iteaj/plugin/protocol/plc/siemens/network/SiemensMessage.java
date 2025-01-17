package com.iteaj.plugin.protocol.plc.siemens.network;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientMessage;

public class SiemensMessage extends PlcClientMessage {

    public SiemensMessage(byte[] message) {
        super(message);
    }
}
