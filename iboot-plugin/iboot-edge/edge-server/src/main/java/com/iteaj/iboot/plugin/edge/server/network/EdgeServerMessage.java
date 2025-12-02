package com.iteaj.iboot.plugin.edge.server.network;

import com.iteaj.iot.server.ServerMessage;

public class EdgeServerMessage extends ServerMessage {

    public EdgeServerMessage(byte[] message) {
        super(message);
    }

    public EdgeServerMessage(MessageHead head) {
        super(head);
    }

    public EdgeServerMessage(MessageHead head, MessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message) {
        return null;
    }
}
