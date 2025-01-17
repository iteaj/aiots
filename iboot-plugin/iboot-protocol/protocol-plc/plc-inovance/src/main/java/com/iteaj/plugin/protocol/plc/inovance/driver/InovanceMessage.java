package com.iteaj.plugin.protocol.plc.inovance.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientMessage;

public class InovanceMessage extends PlcClientMessage {

    public InovanceMessage(byte[] message) {
        super(message);
    }
}
