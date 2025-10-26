package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.server.ServerMessage;
import com.iteaj.iot.utils.ByteUtil;

import java.util.HashMap;
import java.util.Map;

public class HJ212Message extends ServerMessage {
    public HJ212Message(byte[] message) {
        super(message);
    }

    public HJ212Message(HJ212MessageHead head) {
        super(head);
    }

    public HJ212Message(HJ212MessageHead head, HJ212MessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message) {
        String[] split = new String(ByteUtil.subBytes(message, 6, message.length - 6)).split(";");
        Map<HJ212Item, String> body = new HashMap<>(split.length);
        for (String s : split) {
            String[] item = s.split("=");
            body.put(HJ212Item.valueOf(item[0]), item[1]);
        }

        this.setBody(new HJ212MessageBody(body));
        HJ212SysCode byCode = HJ212SysCode.getByCode(getBody().getItem(HJ212Item.ST));
        HJ212CmdCode cmdCode = HJ212CmdCode.getByCode(getBody().getItem(HJ212Item.CN));
        return new HJ212MessageHead(getBody().getItem(HJ212Item.MN), byCode, cmdCode);
    }

    @Override
    public HJ212MessageHead getHead() {
        return (HJ212MessageHead) super.getHead();
    }

    @Override
    public HJ212MessageBody getBody() {
        return (HJ212MessageBody) super.getBody();
    }
}
