package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.Utilities;

public class AdsNetMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 6;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        if (HeadBytes.length >= 6) {
            int length = Utilities.getInt(HeadBytes, 2);
            if (length > 100_000_000) length = 100_000_000;
            if (length < 0) length = 0;
            return length;
        } else
            return 0;
    }

}
