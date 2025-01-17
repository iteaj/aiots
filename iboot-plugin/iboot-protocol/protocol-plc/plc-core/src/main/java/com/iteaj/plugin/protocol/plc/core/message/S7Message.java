package com.iteaj.plugin.protocol.plc.core.message;

public class S7Message extends NetMessageBase implements INetMessage
{
    public int ProtocolHeadBytesLength(){
        return 4;
    }

    public int GetContentLengthByHeadBytes(){
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if(HeadBytes == null) return 0;

        return (HeadBytes[2] & 0xff) * 256 + (HeadBytes[3] & 0xff) - 4;
    }


    public boolean CheckHeadBytesLegal(byte[] token)
    {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if(HeadBytes == null) return false;
        if(HeadBytes[0]==0x03 && HeadBytes[1] == 0x00){
            return true;
        }
        else {
            return false;
        }
    }
}
