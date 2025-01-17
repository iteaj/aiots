package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.types.MemoryStream;

public class NetMessageBase {

    public boolean CheckHeadBytesLegal(byte[] token)
    {
        return true;
    }

    public int GetHeadBytesIdentity(){
        return 0;
    }

    public int PependedUselesByteLength( byte[] headByte ) { return 0; };

    public byte[] getHeadBytes() {
        return HeadBytes;
    }

    public byte[] getContentBytes() {
        return ContentBytes;
    }

    public byte[] getSendBytes() {
        return SendBytes;
    }

    public void setHeadBytes(byte[] headBytes){
        HeadBytes = headBytes;
    }

    public void setContentBytes(byte[] contentBytes){
        ContentBytes = contentBytes;
    }

    public void setSendBytes(byte[] sendBytes){
        SendBytes = sendBytes;
    }

    private byte[] HeadBytes = null;

    private byte[] ContentBytes = null;

    private byte[] SendBytes = null;

    public boolean CheckReceiveDataComplete( byte[] send, MemoryStream ms ) { return true; }

    public int CheckMessageMatch( byte[] send, byte[] receive ) { return  1;}

    public String toString( ) {
        return "NetMessageBase";
    }
}
