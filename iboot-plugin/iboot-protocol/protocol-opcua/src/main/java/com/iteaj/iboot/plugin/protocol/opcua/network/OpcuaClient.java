package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.TcpSocketClient;
import com.iteaj.iot.client.component.TcpClientComponent;
import com.iteaj.iot.event.ClientStatus;
import com.iteaj.iot.event.StatusEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandler;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.spi.connection.DefaultNettyPlcConnection;

public class OpcuaClient extends TcpSocketClient {

    private DefaultNettyPlcConnection plcConnection;
    private final static PlcDriverManager driverManager = new PlcDriverManager();

    public OpcuaClient(TcpClientComponent clientComponent, ClientConnectProperties config) {
        super(clientComponent, config);
    }

    @Override
    public void init(Object config) {
        try {
            ClientConnectProperties properties = getConfig();
            plcConnection = (DefaultNettyPlcConnection) driverManager
                    .getConnection(String.format("opcua://%s:%d", properties.getHost(), properties.getPort()));
        } catch (PlcConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChannelFuture connect() {
        try {
            plcConnection.connect();
            finishedConnect(getChannel().newSucceededFuture());
        } catch (PlcConnectionException e) {
            return getChannel().newFailedFuture(e);
        }

        return getChannel().newSucceededFuture();
    }

    @Override
    public ChannelFuture close() {
        return disconnect().addListener(future -> {
            if (future.isSuccess()) {
                getClientComponent().removeClient(getConfig());
            }
        });
    }

    @Override
    public ChannelFuture disconnect() {
        try {
            plcConnection.close();
        } catch (PlcConnectionException e) {
            return getChannel().newFailedFuture(e);
        }

        // 发布客户端上线事件
        FrameworkManager.publishEvent(new StatusEvent(this, ClientStatus.offline, getClientComponent()));
        return getChannel().newSucceededFuture();
    }

    @Override
    protected ChannelInboundHandler createProtocolDecoder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Channel getChannel() {
        return plcConnection.getChannel();
    }
}
