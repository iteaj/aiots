package com.iteaj.plugin.protocol.iec104.network;

import com.iteaj.iot.client.ClientMessage;

public class IEC104Message extends ClientMessage {
    public IEC104Message(byte[] message) {
        super(message);
    }

    public IEC104Message(MessageHead head) {
        super(head);
    }

    public IEC104Message(MessageHead head, MessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message) {
        return null;
    }
}
