package com.iteaj.iboot.plugin.edge.client.network;

import com.iteaj.iboot.plugin.edge.core.network.EdgeProtocolType;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.protocol.ClientInitiativeProtocol;

public class EdgeClientHeartbeatProtocol extends ClientInitiativeProtocol<EdgeClientMessage> {

    private static final EdgeClientHeartbeatProtocol instance = new EdgeClientHeartbeatProtocol();

    public static EdgeClientHeartbeatProtocol getInstance() {
        return instance;
    }

    @Override
    protected EdgeClientMessage doBuildRequestMessage() {
        return null;
    }

    @Override
    public void doBuildResponseMessage(EdgeClientMessage responseMessage) {

    }

    @Override
    public ProtocolType protocolType() {
        return EdgeProtocolType.heartbeat;
    }
}
