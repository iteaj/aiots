package com.iteaj.plugin.protocol.plc.core.message;

import java.nio.charset.StandardCharsets;

public class MelsecQnA3EAsciiMessage extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 18;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        if (HeadBytes == null) return 0;

        byte[] buffer = new byte[4];
        buffer[0] = HeadBytes[14];
        buffer[1] = HeadBytes[15];
        buffer[2] = HeadBytes[16];
        buffer[3] = HeadBytes[17];

        return Integer.parseInt(new String(buffer, StandardCharsets.US_ASCII), 16);
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = getHeadBytes();
        if (HeadBytes == null) return false;
        if (HeadBytes[0] == (byte) 'D' && HeadBytes[1] == (byte) '0' && HeadBytes[2] == (byte) '0' && HeadBytes[3] == (byte) '0') {
            return true;
        } else {
            return false;
        }
    }

}
