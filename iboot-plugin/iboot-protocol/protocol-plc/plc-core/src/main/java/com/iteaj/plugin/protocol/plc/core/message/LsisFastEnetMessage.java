package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.Utilities;

public class LsisFastEnetMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 20;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        if (HeadBytes.length >= 20) {
            return Utilities.getShort(HeadBytes, 16);
        } else
            return 0;
    }

    public boolean CheckHeadBytesLegal(byte[] token)
    {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        return HeadBytes[0] == (byte) 0x4C;
    }

    public int GetHeadBytesIdentity(){
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        return Utilities.getUShort(HeadBytes, 14);
    }

}
