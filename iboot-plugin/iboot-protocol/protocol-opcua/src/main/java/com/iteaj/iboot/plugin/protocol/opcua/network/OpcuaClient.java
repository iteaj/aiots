package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.TcpSocketClient;
import com.iteaj.iot.client.component.TcpClientComponent;
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
            plcConnection = (DefaultNettyPlcConnection) driverManager.getConnection("");
        } catch (PlcConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChannelFuture connect() {
        return super.connect();
    }

    @Override
    protected ChannelInboundHandler createProtocolDecoder() {
        return null;
    }

    @Override
    public Channel getChannel() {
        return plcConnection.getChannel();
    }
}
