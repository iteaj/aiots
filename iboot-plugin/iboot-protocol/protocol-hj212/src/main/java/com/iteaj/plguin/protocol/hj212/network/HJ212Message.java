package com.iteaj.plguin.protocol.hj212.network;

import cn.hutool.core.util.StrUtil;
import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.server.ServerMessage;
import com.iteaj.iot.utils.ByteUtil;
import org.jetbrains.annotations.NotNull;

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
        byte[] subMsg = ByteUtil.subBytes(message, 6, message.length - 6);

        String strMsg = new String(subMsg);
        int indexOf = strMsg.indexOf("CP=&&");
        if(indexOf > 0) {
            String heads = strMsg.substring(0, indexOf);
            String[] split = heads.split(";");
            Map<HJ212Item, String> head = new HashMap<>(split.length);
            for (String s : split) {
                String[] item = s.split("=");
                head.put(HJ212Item.valueOf(item[0]), item[1]);
            }

            String params = strMsg.substring(indexOf + 5, strMsg.length() - 2);
            if(StrUtil.isNotBlank(params)) {
                Map<String, String> body = getParams(params);
                this.setBody(new HJ212MessageBody(body));
            }

            HJ212SysCode byCode = HJ212SysCode.getByCode(head.get(HJ212Item.ST));
            HJ212CmdCode cmdCode = HJ212CmdCode.getByCode(head.get(HJ212Item.CN));
            if(logger.isDebugEnabled()) {
                logger.debug("收到HJ212指令: {} - 报文: {}", cmdCode, new String(message));
            }
            return new HJ212MessageHead(head.get(HJ212Item.MN), byCode, cmdCode, head.get(HJ212Item.QN), head.get(HJ212Item.PW), head.get(HJ212Item.Flag));
        } else {
            throw new ProtocolException("报文不完整错误(未包含CP=&&): " + strMsg);
        }
    }

    @NotNull
    private static Map<String, String> getParams(String params) {
        Map<String, String> body = new HashMap<>();
        String[] items = params.split(";");
        for (String item : items) {
            if(item.contains(",")) {
                String[] detailItems = item.split(",");
                for (String detailItem : detailItems) {
                    String[] detailValueItem = detailItem.split("=");
                    body.put(detailValueItem[0], detailValueItem[1]);
                }
            } else {
                String[] valueItem = item.split("=");
                body.put(valueItem[0], valueItem[1]);
            }
        }

        return body;
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
