package com.iteaj.plugin.protocol.plc.core.message;

public class KukaVarProxyMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 4;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        if (HeadBytes.length < 4) return 0;
        return (HeadBytes[2]& 0xff) * 256 + HeadBytes[3]& 0xff;
    }

    public boolean CheckHeadBytesLegal(byte[] token)
    {
        return true;
    }

    public int GetHeadBytesIdentity(){
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        return (HeadBytes[0] & 0xff) * 256 + (HeadBytes[1]&0xff);
    }

}
