package com.iteaj.iboot.msn.iot.server.debug;

import com.iteaj.iboot.msn.iot.server.debug.protocol.DebugProtocol;
import com.iteaj.iot.AbstractProtocol;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.server.component.ByteToMessageDecoderServerComponent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * 监听6158端口作为调试端口
 */
public class DebugServerComponent extends ByteToMessageDecoderServerComponent<DebugServerMessage> {

    public DebugServerComponent(ConnectProperties connectProperties) {
        super(connectProperties);
    }

    @Override
    public String getName() {
        return "服务端调试";
    }

    @Override
    public String getDesc() {
        return "自定义报文解码组件";
    }

    @Override
    public AbstractProtocol getProtocol(DebugServerMessage message) {
        return new DebugProtocol(message);
    }

    @Override
    public DebugServerMessage doTcpDecode(ChannelHandlerContext ctx, ByteBuf in) {
        final int readableBytes = in.readableBytes();
        if(readableBytes > 0) {
            byte[] message = new byte[readableBytes];
            in.readBytes(message);
            return createMessage(message);
        } else {
            return null;
        }
    }

    @Override
    public DebugServerMessage createMessage(byte[] message) {
        return new DebugServerMessage(message);
    }
}
