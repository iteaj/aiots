package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.client.ClientMessage;

public class OpcuaMessage extends ClientMessage {

    public OpcuaMessage(byte[] message) {
        super(message);
    }

    public OpcuaMessage(MessageHead head) {
        super(head);
    }

    public OpcuaMessage(MessageHead head, MessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message) {
        return null;
    }
}
