package com.iteaj.iboot.plugin.edge.client.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.component.LengthFieldBasedFrameClientComponent;
import com.iteaj.iot.client.protocol.ServerInitiativeProtocol;

public class EdgeClientComponent extends LengthFieldBasedFrameClientComponent<EdgeClientMessage> {

    public EdgeClientComponent(ClientConnectProperties config, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(config, maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected ServerInitiativeProtocol<EdgeClientMessage> doGetProtocol(EdgeClientMessage message, ProtocolType type) {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDesc() {
        return "";
    }
}
