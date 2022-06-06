package com.iteaj.iboot.msn.iot.server.debug.protocol;

import com.iteaj.iboot.msn.iot.server.debug.DebugProtocolType;
import com.iteaj.iboot.msn.iot.server.debug.DebugServerMessage;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;

public class DebugProtocol extends ClientInitiativeProtocol<DebugServerMessage> {

    private DebugServerMessage responseMessage;

    public DebugProtocol(DebugServerMessage requestMessage) {
        super(requestMessage);
    }

    public void response(byte[] message) {
        this.responseMessage = new DebugServerMessage(message);
    }

    @Override
    protected DebugServerMessage doBuildResponseMessage() {
        return responseMessage;
    }

    @Override
    protected void doBuildRequestMessage(DebugServerMessage requestMessage) { }

    @Override
    public ProtocolType protocolType() {
        return DebugProtocolType.SERVER_DEBUG;
    }
}
