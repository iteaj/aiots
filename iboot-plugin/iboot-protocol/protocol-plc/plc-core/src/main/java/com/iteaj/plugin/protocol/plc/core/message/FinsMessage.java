package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.Utilities;

public class FinsMessage extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 8;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        byte[] buffer = new byte[4];
        buffer[0] = HeadBytes[7];
        buffer[1] = HeadBytes[6];
        buffer[2] = HeadBytes[5];
        buffer[3] = HeadBytes[4];
        int length = Utilities.getInt(buffer, 0);
        if (length > 10000) length = 10000;
        if (length < 0) length = 0;
        return length;
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return false;
        if (HeadBytes[0] == 0x46 && HeadBytes[1] == 0x49 && HeadBytes[2] == 0x4E && HeadBytes[3] == 0x53) {
            return true;
        } else {
            return false;
        }
    }
}
