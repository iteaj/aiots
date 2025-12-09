package com.iteaj.iboot.plugin.protocol.dtu.priv;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.dtu.DtuCommonProtocolType;
import com.iteaj.iot.server.dtu.impl.CommonDtuServerMessage;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;

public class DtuInitiativeProtocol extends ClientInitiativeProtocol<CommonDtuServerMessage> {

    public DtuInitiativeProtocol(CommonDtuServerMessage requestMessage) {
        super(requestMessage);
    }

    @Override
    protected CommonDtuServerMessage doBuildResponseMessage() {
        return null;
    }

    @Override
    protected void doBuildRequestMessage(CommonDtuServerMessage requestMessage) {

    }

    @Override
    public ProtocolType protocolType() {
        return DtuCommonProtocolType.DTU;
    }
}
