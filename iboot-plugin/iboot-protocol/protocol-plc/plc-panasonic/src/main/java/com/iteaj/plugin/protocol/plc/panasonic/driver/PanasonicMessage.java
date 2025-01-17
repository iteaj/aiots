package com.iteaj.plugin.protocol.plc.panasonic.driver;

import com.iteaj.plugin.protocol.plc.adapt.PlcClientMessage;

public class PanasonicMessage extends PlcClientMessage {

    public PanasonicMessage(byte[] message) {
        super(message);
    }
}
