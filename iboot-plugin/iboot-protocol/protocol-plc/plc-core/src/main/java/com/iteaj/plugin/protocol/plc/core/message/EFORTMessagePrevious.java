package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.Utilities;

public class EFORTMessagePrevious extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 17;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        int length = Utilities.getShort(HeadBytes, 15) - 17;
        if (length < 0) length = 0;
        return length;
    }

}
