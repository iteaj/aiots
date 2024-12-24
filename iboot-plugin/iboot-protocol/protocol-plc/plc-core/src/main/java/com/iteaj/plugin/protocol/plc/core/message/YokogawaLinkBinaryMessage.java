package com.iteaj.plugin.protocol.plc.core.message;


/**
 * 横河PLC的以太网的二进制协议
 */
public class YokogawaLinkBinaryMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength() {
        return 4;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;

        return (HeadBytes[2] & 0xff) * 256 + HeadBytes[3] & 0xff;
    }

}
