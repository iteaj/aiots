package com.iteaj.plugin.protocol.plc.core.message;

public class GeSRTPMessage extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 56;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        return (HeadBytes[4] & 0xff) + (HeadBytes[5] & 0xff) * 256;
    }

}
