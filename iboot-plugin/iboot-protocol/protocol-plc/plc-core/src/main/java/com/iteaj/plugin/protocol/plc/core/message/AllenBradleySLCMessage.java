package com.iteaj.plugin.protocol.plc.core.message;

public class AllenBradleySLCMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 28;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        return (HeadBytes[2] & 0xff) * 256 + (HeadBytes[3] & 0xff);
    }

}
