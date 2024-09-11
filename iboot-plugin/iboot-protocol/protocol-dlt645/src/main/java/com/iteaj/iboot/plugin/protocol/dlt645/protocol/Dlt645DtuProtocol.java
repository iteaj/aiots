package com.iteaj.iboot.plugin.protocol.dlt645.protocol;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.protocol.ServerInitiativeSyncProtocol;

import java.io.IOException;

public class Dlt645DtuProtocol extends ServerInitiativeSyncProtocol<Dlt645DtuMessage> {

    private Dlt645ProtocolType protocolType;

    @Override
    protected Dlt645DtuMessage doBuildRequestMessage() throws IOException {
        return null;
    }

    @Override
    protected void doBuildResponseMessage(Dlt645DtuMessage message) {

    }

    @Override
    public ProtocolType protocolType() {
        return this.protocolType;
    }
}
