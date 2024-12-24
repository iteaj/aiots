package com.iteaj.plugin.protocol.plc.adapt;

import com.iteaj.iot.client.ClientMessage;

public abstract class PlcClientMessage extends ClientMessage {

    public PlcClientMessage(byte[] message) {
        super(message);
    }

    @Override
    protected MessageHead doBuild(byte[] message) {
        return null;
    }
}
