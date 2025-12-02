package com.iteaj.iboot.plugin.edge.client.network;

import com.iteaj.iot.client.ClientMessage;

public class EdgeClientMessage extends ClientMessage {

    public EdgeClientMessage(byte[] message) {
        super(message);
    }

    public EdgeClientMessage(MessageHead head) {
        super(head);
    }

    public EdgeClientMessage(MessageHead head, MessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message) {
        return null;
    }
}
