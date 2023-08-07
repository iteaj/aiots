package com.iteaj.iboot.module.iot.rtu;

import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.TcpSocketClient;
import com.iteaj.iot.client.component.TcpClientComponent;
import com.iteaj.iot.codec.adapter.ByteToMessageDecoderAdapter;
import io.netty.channel.ChannelInboundHandler;

public class ModbusRtuClient extends TcpSocketClient {

    public ModbusRtuClient(TcpClientComponent clientComponent, ClientConnectProperties config) {
        super(clientComponent, config);
    }

    @Override
    protected ChannelInboundHandler createProtocolDecoder() {
        return new ByteToMessageDecoderAdapter(this.getClientComponent());
    }
}
