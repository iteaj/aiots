package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.Utilities;

public class FanucRobotMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 56;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        return Utilities.getUShort(HeadBytes, 4);
    }

}
