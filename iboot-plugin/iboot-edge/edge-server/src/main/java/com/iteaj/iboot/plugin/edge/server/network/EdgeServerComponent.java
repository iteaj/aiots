package com.iteaj.iboot.plugin.edge.server.network;

import com.iteaj.iboot.plugin.edge.core.EdgeConfigProperties;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.server.component.LengthFieldBasedFrameDecoderServerComponent;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;
import org.springframework.stereotype.Component;

@Component
public class EdgeServerComponent extends LengthFieldBasedFrameDecoderServerComponent<EdgeServerMessage> {

    private static final String NAME = "EDGE_SERVER";
    private static final String DESC = "作为云边协同系统的服务端实现";

    public EdgeServerComponent(EdgeConfigProperties edgeConfig) {
        super(new ConnectProperties(), Short.MAX_VALUE, 2, 2);
        EdgeConfigProperties.ServerConfig server = edgeConfig.getServer();
        config().setHost(server.getHost()); config().setPort(edgeConfig.getPort());
        config().setReaderIdleTime(edgeConfig.getReadIdle());
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
    public EdgeServerMessage createMessage(byte[] message) {
        return new EdgeServerMessage(message);
    }

    @Override
    public Class<EdgeServerMessage> getMessageClass() {
        return EdgeServerMessage.class;
    }

    @Override
    protected ClientInitiativeProtocol<EdgeServerMessage> doGetProtocol(EdgeServerMessage message, ProtocolType type) {
        return null;
    }
}
