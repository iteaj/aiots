package com.iteaj.plugin.protocol.plc.core.message;

public class AlienMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 5;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        return HeadBytes[4] & 0xFFFF;
    }

    public boolean CheckHeadBytesLegal(byte[] token)
    {
        return true;
    }

}
