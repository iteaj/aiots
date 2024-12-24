package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.types.Convert;
import com.iteaj.plugin.protocol.plc.core.types.Encoding;

public class OpenProtocolMessage extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength(){
        return 4;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        try {
            if (HeadBytes.length >= 4) {
                int length = Convert.ToInt32(Encoding.ASCII.GetString(HeadBytes, 0, 4)) - 4 + 1;

                return Math.max(length, 0);
            } else
                return 0;
        } catch (Exception ex) {
            return 16 + 1;
        }
    }
}
