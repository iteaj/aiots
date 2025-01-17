package com.iteaj.plugin.protocol.plc.core.message;

public class MelsecQnA3EBinaryMessage extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 9;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        return (HeadBytes[7] & 0xff) + (HeadBytes[8] & 0xff) * 256;
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return false;
        if ((HeadBytes[0] & 0xff) == 0xd0 && HeadBytes[1] == 0x00) {
            return true;
        } else {
            return false;
        }
    }

}
