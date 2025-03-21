package com.iteaj.iboot.plugin.protocol.dlt645.network.dtu;

import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645MessageHead;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iot.server.dtu.DefaultDtuMessageAware;
import com.iteaj.iot.server.dtu.DtuCommonProtocolType;
import com.iteaj.iot.server.dtu.DtuMessageType;
import io.netty.buffer.ByteBuf;

public class Dlt645DtuMessageAware extends DefaultDtuMessageAware<Dlt645DtuMessage> {

    public Dlt645DtuMessageAware(DtuMessageType messageType) {
        super(messageType);
    }

    @Override
    protected Dlt645DtuMessage customizeType(String equipCode, byte[] message, ByteBuf msg) {
        int prefix = msg.bytesBefore((byte) 0x68);
        if(prefix == -1) {
            return null; // 不能判断
        }

        // 获取长度字节
        if(msg.readableBytes() < prefix + 10) {
            return null; // 未获取长度字段不能判断
        }

        int length = message[prefix + 10 - 1] & 0xFF;
        int totalLength = length + 10 + prefix + 2;
        if(msg.readableBytes() < totalLength) {
            return null;
        }

        byte[] fullMessage = new byte[totalLength];
        msg.readBytes(fullMessage);

        Dlt645DtuMessage responseMessage = createMessage(fullMessage);
        responseMessage.setHead(new Dlt645MessageHead(equipCode, null, null, prefix));
        return responseMessage;
    }
}
