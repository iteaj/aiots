package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.Utilities;

public class EFORTMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 18;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        int length = Utilities.getShort(HeadBytes, 16) - 18;
        if (length < 0) length = 0;
        return length;
    }

}
