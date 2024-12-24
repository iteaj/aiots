package com.iteaj.plugin.protocol.plc.core.message;

public class FetchWriteMessage extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 16;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();

        if (HeadBytes[5] == 0x05 || HeadBytes[5] == 0x04) return 0;
        if (HeadBytes[5] == 0x06) {
            if (SendBytes == null) return 0;
            if (HeadBytes[8] != 0x00) return 0; // 发生了错误
            if (SendBytes[8] == 0x01 || SendBytes[8] == 0x06 || SendBytes[8] == 0x07)
                return ((SendBytes[12] & 0xff) * 256 + (SendBytes[13] & 0xff)) * 2;
            return (SendBytes[12] & 0xff) * 256 + (SendBytes[13] & 0xff);
        } else if (HeadBytes[5] == 0x03) {
            if (HeadBytes[8] == 0x01 || HeadBytes[8] == 0x06 || HeadBytes[8] == 0x07)
                return ((HeadBytes[12] & 0xff) * 256 + (HeadBytes[13] & 0xff)) * 2;
            return (HeadBytes[12] & 0xff) * 256 + (HeadBytes[13] & 0xff);
        }
        return 0;
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return false;
        if (HeadBytes[0] == 0x53 && HeadBytes[1] == 0x35) {
            return true;
        } else {
            return false;
        }
    }

    public int GetHeadBytesIdentity()
    {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        return HeadBytes[3];
    }
}
