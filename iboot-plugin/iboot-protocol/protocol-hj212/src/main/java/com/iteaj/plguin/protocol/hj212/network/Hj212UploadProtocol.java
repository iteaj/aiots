package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;

public class Hj212UploadProtocol extends ClientInitiativeProtocol<HJ212Message> {

    public Hj212UploadProtocol(HJ212Message requestMessage) {
        super(requestMessage);
    }

    @Override
    protected HJ212Message doBuildResponseMessage() {
        return null;
    }

    @Override
    protected void doBuildRequestMessage(HJ212Message requestMessage) {

    }

    @Override
    public ProtocolType protocolType() {
        return requestMessage().getHead().getType();
    }
}
