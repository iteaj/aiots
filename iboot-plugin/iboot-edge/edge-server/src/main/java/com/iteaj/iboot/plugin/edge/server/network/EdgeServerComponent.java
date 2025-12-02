package com.iteaj.iboot.plugin.edge.server.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.server.component.LengthFieldBasedFrameDecoderServerComponent;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;

public class EdgeServerComponent extends LengthFieldBasedFrameDecoderServerComponent<EdgeServerMessage> {

    private static final String NAME = "边缘服务端";
    private static final String DESC = "作为云边协同系统的服务端实现";

    public EdgeServerComponent(ConnectProperties connectProperties) {
        super(connectProperties, Short.MAX_VALUE, 2, 2);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDesc() {
        return DESC;
    }

    @Override
    protected ClientInitiativeProtocol<EdgeServerMessage> doGetProtocol(EdgeServerMessage message, ProtocolType type) {
        return null;
    }
}
