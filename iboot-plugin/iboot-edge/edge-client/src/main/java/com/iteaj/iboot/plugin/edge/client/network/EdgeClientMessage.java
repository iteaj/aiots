package com.iteaj.iboot.plugin.edge.client.network;

import com.iteaj.iboot.plugin.edge.core.network.EdgeMessageBody;
import com.iteaj.iboot.plugin.edge.core.network.EdgeMessageHead;
import com.iteaj.iboot.plugin.edge.core.network.EdgeProtocolType;
import com.iteaj.iot.client.ClientMessage;
import com.iteaj.iot.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static com.iteaj.iboot.plugin.edge.core.network.EdgeMessageHead.MAGIC_NUM;

public class EdgeClientMessage extends ClientMessage {

    public EdgeClientMessage(byte[] message) {
        super(message);
    }

    public EdgeClientMessage(EdgeMessageHead head) {
        super(head);
    }

    public EdgeClientMessage(EdgeMessageHead head, EdgeMessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message) {
        byte type = message[3];
        String msgId = ByteUtil.bytesToHex(ByteUtil.subBytes(message, 6, 10));
        if(type == EdgeProtocolType.heartbeat.getCode()) {
            return new EdgeMessageHead(EdgeProtocolType.heartbeat, msgId, message);
        }

        return null;
    }

    public static ByteBuf buildRegister(String equipCode) {
        byte[] equipCodeBytes = ByteUtil.hexToByte(equipCode);
        byte[] header = new byte[] {MAGIC_NUM, 0x00, 0x0D, EdgeProtocolType.register.getCode(), 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
        return Unpooled.wrappedBuffer(header, equipCodeBytes);
    }

    public static ByteBuf buildHeartbeat() {
        byte[] header = new byte[] {MAGIC_NUM, 0x00, 0x07, EdgeProtocolType.heartbeat.getCode(), 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
        return Unpooled.wrappedBuffer(header);
    }
}
