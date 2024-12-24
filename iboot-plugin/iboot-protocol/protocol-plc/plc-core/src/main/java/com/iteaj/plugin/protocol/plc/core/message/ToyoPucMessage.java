package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.types.BitConverter;

public class ToyoPucMessage extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 4;
    }

    public int GetContentLengthByHeadBytes() {
        try {
            byte[] HeadBytes = getHeadBytes();
            if (HeadBytes == null) return 0;
            if (HeadBytes.length >= 4) {
                return BitConverter.ToUInt16(HeadBytes, 2);
            } else
                return 0;
        } catch (Exception ex) {
            return 0;
        }
    }
}
