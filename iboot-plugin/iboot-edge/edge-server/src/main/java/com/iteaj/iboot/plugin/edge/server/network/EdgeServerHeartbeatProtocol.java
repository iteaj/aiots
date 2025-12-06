package com.iteaj.iboot.plugin.edge.server.network;

import com.iteaj.iboot.plugin.edge.core.network.EdgeProtocolType;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;

public class EdgeServerHeartbeatProtocol extends ClientInitiativeProtocol<EdgeServerMessage> {

    public EdgeServerHeartbeatProtocol(EdgeServerMessage requestMessage) {
        super(requestMessage);
    }

    @Override
    protected EdgeServerMessage doBuildResponseMessage() {
        requestMessage.getMessage()[4] = 0; // 请求报文方向设置为响应
        return requestMessage;
    }

    @Override
    protected void doBuildRequestMessage(EdgeServerMessage requestMessage) {

    }

    @Override
    public EdgeProtocolType protocolType() {
        return EdgeProtocolType.heartbeat;
    }
}
