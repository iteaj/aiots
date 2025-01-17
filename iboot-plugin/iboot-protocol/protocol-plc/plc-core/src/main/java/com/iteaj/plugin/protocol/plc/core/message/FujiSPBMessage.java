package com.iteaj.plugin.protocol.plc.core.message;

import java.nio.charset.StandardCharsets;

/**
 * 富士SPB的消息内容
 */
public class FujiSPBMessage extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 5;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        return Integer.parseInt(new String(HeadBytes, 3, 2, StandardCharsets.US_ASCII), 16) * 2 + 2;
    }

}
