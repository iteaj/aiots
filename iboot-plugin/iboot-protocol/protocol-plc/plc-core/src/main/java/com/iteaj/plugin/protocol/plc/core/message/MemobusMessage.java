package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.types.BitConverter;

/**
 * Memobus协议的消息定义
 */
public class MemobusMessage  extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 12;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        if (HeadBytes == null) return 0;
        if (HeadBytes.length >= ProtocolHeadBytesLength()) {
            int length = BitConverter.ToUInt16(getHeadBytes(), 6) - 12;
            if (length < 0) length = 0;
            return length;
        } else {
            return 0;
        }
    }
}
