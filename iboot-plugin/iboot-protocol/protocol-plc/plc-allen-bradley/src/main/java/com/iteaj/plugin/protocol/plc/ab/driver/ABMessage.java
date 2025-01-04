package com.iteaj.plugin.protocol.plc.ab.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientMessage;

public class ABMessage extends PlcClientMessage {

    public ABMessage(byte[] message) {
        super(message);
    }
}
