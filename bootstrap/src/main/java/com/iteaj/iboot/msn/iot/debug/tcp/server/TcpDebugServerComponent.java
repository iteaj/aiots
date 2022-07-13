package com.iteaj.iboot.msn.iot.debug.tcp.server;

import com.iteaj.iboot.msn.iot.debug.tcp.server.protocol.DebugProtocol;
import com.iteaj.iot.AbstractProtocol;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.server.component.SimpleChannelDecoderServerComponent;

/**
 * 监听6158端口作为调试端口
 */
public class TcpDebugServerComponent extends SimpleChannelDecoderServerComponent<DebugServerMessage> {

    public TcpDebugServerComponent(ConnectProperties connectProperties) {
        super(connectProperties);
    }

    @Override
    public String getName() {
        return "TCP服务端调试";
    }

    @Override
    public String getDesc() {
        return "用于实现对tcp服务组件的调试";
    }

    @Override
    public AbstractProtocol getProtocol(DebugServerMessage message) {
        return new DebugProtocol(message);
    }

    @Override
    public DebugServerMessage createMessage(byte[] message) {
        return new DebugServerMessage(message);
    }
}
