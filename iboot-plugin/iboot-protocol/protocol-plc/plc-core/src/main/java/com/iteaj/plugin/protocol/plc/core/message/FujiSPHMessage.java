package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.Utilities;

public class FujiSPHMessage extends NetMessageBase implements INetMessage{
    public int ProtocolHeadBytesLength() {
        return 20;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        return Utilities.getUShort(HeadBytes, 18);
    }

}
