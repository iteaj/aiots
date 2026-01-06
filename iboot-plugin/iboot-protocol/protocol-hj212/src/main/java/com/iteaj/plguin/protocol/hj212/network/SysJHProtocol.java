package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;

public class SysJHProtocol extends ClientInitiativeProtocol<HJ212Message> {

    public SysJHProtocol(HJ212Message requestMessage) {
        super(requestMessage);
    }

    @Override
    protected HJ212Message doBuildResponseMessage() {
        HJ212CmdCode cmdCode = protocolType();

        return null;
    }

    @Override
    protected void doBuildRequestMessage(HJ212Message requestMessage) {

    }

    @Override
    public HJ212CmdCode protocolType() {
        return requestMessage.getHead().getType();
    }
}
