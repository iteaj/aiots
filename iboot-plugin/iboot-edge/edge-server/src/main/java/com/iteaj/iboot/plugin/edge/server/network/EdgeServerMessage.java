package com.iteaj.iboot.plugin.edge.server.network;

import com.iteaj.iboot.plugin.edge.core.network.EdgeMessageBody;
import com.iteaj.iboot.plugin.edge.core.network.EdgeMessageHead;
import com.iteaj.iboot.plugin.edge.core.network.EdgeProtocolType;
import com.iteaj.iot.server.ServerMessage;
import com.iteaj.iot.utils.ByteUtil;

public class EdgeServerMessage extends ServerMessage {

    public EdgeServerMessage(byte[] message) {
        super(message);
    }

    public EdgeServerMessage(EdgeMessageHead head) {
        super(head);
    }

    public EdgeServerMessage(EdgeMessageHead head, EdgeMessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message) {
        byte type = message[3];
        String msgId = ByteUtil.bytesToHex(ByteUtil.subBytes(message, 6, 10));
        // 注册包
        if(type == EdgeProtocolType.register.getCode()) {
            String mac = ByteUtil.bytesToHex(ByteUtil.subBytes(message, 10));
            return new EdgeMessageHead(mac, EdgeProtocolType.register, msgId, message);
        } else if(type == EdgeProtocolType.heartbeat.getCode()) {
            return new EdgeMessageHead(EdgeProtocolType.heartbeat, msgId, message);
        }

        return new EdgeMessageHead(EdgeProtocolType.getByCode(type), msgId, message);
    }
}
