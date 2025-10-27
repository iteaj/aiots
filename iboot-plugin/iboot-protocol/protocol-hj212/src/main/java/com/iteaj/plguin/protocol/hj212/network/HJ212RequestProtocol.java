package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.protocol.ServerInitiativeProtocol;

import java.io.IOException;

public class HJ212RequestProtocol extends ServerInitiativeProtocol<HJ212Message> {

    private HJ212CmdCode cmdCode;

    @Override
    protected HJ212Message doBuildRequestMessage() throws IOException {
        return null;
    }

    @Override
    protected void doBuildResponseMessage(HJ212Message message) {

    }

    @Override
    public ProtocolType protocolType() {
        return cmdCode;
    }
}
