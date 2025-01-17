package com.iteaj.plugin.protocol.plc.core.message;

public class SAMMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 7;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        if (HeadBytes.length >= 7) {
            return (HeadBytes[5] & 0xff) * 256 + (HeadBytes[6] & 0xff);
        } else
            return 0;
    }

    public boolean CheckHeadBytesLegal(byte[] token)
    {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return false;

        return HeadBytes[0] == (byte) 0xAA && HeadBytes[1] == (byte)0xAA && HeadBytes[2] == (byte)0xAA && HeadBytes[3] == (byte)0x96 && HeadBytes[4] == (byte)0x69;
    }

}
