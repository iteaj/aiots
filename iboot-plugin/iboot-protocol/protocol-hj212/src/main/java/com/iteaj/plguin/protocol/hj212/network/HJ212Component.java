package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.server.component.ByteToMessageDecoderServerComponent;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;
import com.iteaj.iot.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

public class HJ212Component extends ByteToMessageDecoderServerComponent<HJ212Message> {

    private static final String NAME = "HJ212";
    private static final String DESC = "国家环境监测协议标准实现";

    public HJ212Component(ConnectProperties connectProperties) {
        super(connectProperties);
    }

    @Override
    public HJ212Message doTcpDecode(ChannelHandlerContext ctx, ByteBuf in) {
        if(in.isReadable() && in.readableBytes() >= 2) {
            if(in.getByte(0) == '#' && in.getByte(1) == '#') {
                if(in.readableBytes() >= 6) {
                    CharSequence sequence = in.getCharSequence(2, 4, Charset.defaultCharset());
                    int length = Integer.parseInt(sequence.toString());
                    if(in.readableBytes() >= (length + 12)) {
                        byte[] message = new byte[length + 12];
                        in.readBytes(message);
                        return new HJ212Message(message);
                    }
                }
            } else { // 当前报文是错误报文
                byte[] bytes = new byte[in.readableBytes()];
                in.readBytes(bytes);
                logger.error("解码异常({}) 报文内容异常", getName(), new ProtocolException(ByteUtil.bytesToString(bytes)));
            }
        }

        return null;
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
    protected ClientInitiativeProtocol<HJ212Message> doGetProtocol(HJ212Message message, ProtocolType type) {
        return null;
    }
}
