package com.iteaj.iboot.plugin.edge.client.network;

import com.iteaj.iboot.plugin.edge.core.EdgeConfigProperties;
import com.iteaj.iboot.plugin.edge.core.network.EdgeProtocolType;
import com.iteaj.iot.AbstractProtocol;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.TcpSocketClient;
import com.iteaj.iot.client.component.LengthFieldBasedFrameClientComponent;
import com.iteaj.iot.client.protocol.ServerInitiativeProtocol;
import com.iteaj.iot.codec.filter.DecoderInterceptor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleState;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class EdgeClientComponent extends LengthFieldBasedFrameClientComponent<EdgeClientMessage> {

    private static final String NAME = "EDGE_CLIENT";
    private static final String DESC = "作为云边协同系统的边缘网关";

    public EdgeClientComponent(EdgeConfigProperties properties) {
        super(getConfig(properties), Short.MAX_VALUE, 1, 2);
        this.setInterceptor(new DecoderInterceptor<LengthFieldBasedFrameClientComponent<EdgeClientMessage>>() {
            @Override
            public boolean isActivation(Channel channel, LengthFieldBasedFrameClientComponent<EdgeClientMessage> component) {
                ByteBuf byteBuf = EdgeClientMessage.buildRegister(properties.getClient().getDeviceSn());
                channel.writeAndFlush(byteBuf);
                return DecoderInterceptor.super.isActivation(channel, component);
            }
        });
    }

    @NotNull
    private static ClientConnectProperties getConfig(EdgeConfigProperties properties) {
        ClientConnectProperties clientConnectProperties = new ClientConnectProperties(properties.getClient().getRemote()
                , properties.getPort(), properties.getClient().getHost(), null);
        clientConnectProperties.setReaderIdleTime(properties.getReadIdle());
        clientConnectProperties.setWriterIdleTime(properties.getReadIdle() / 2);
        return clientConnectProperties;
    }

    @Override
    public AbstractProtocol<EdgeClientMessage> getProtocol(EdgeClientMessage message) {
        EdgeProtocolType type = message.getHead().getType();
        if(type == EdgeProtocolType.heartbeat) {
            return EdgeClientHeartbeatProtocol.getInstance();
        }
        return null;
    }

    @Override
    public Object idle(String deviceSn, IdleState state) {
        if(state == IdleState.WRITER_IDLE) {
            return EdgeClientMessage.buildHeartbeat();
        } else {
            return state;
        }
    }

    @Override
    protected ServerInitiativeProtocol<EdgeClientMessage> doGetProtocol(EdgeClientMessage message, ProtocolType type) {
        return null;
    }

    @Override
    public TcpSocketClient createNewClient(ClientConnectProperties config) {
        return super.createNewClient(config);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDesc() {
        return DESC;
    }
}
