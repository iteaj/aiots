package com.iteaj.plugin.protocol.plc.core.message;

public class MelsecA1EBinaryMessage extends NetMessageBase implements INetMessage {

    public int ProtocolHeadBytesLength(){
        return 2;
    }

    public int GetContentLengthByHeadBytes(){
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes[1] == 0x5B) return 2;                           // 异常代码 + 0x00
        else if (HeadBytes[1] == 0x00)
        {
            switch (HeadBytes[0])
            {
                case (byte)0x80: return SendBytes[10] != 0x00 ? ((SendBytes[10] & 0xff) + 1) / 2 : 128;            // 位单位成批读出后，回复副标题
                case (byte)0x81: return (SendBytes[10] & 0xff ) * 2;                                                // 字单位成批读出后，回复副标题
                case (byte)0x82:                                                                          // 位单位成批写入后，回复副标题
                case (byte)0x83: return 0;                                                                // 字单位成批写入后，回复副标题
                default: return 0;
            }
        }
        else
            return 0;

        //在A兼容1E协议中，写入值后，若不发生异常，只返回副标题 + 结束代码(0x00)
        //这已经在协议头部读取过了，后面要读取的长度为0（contentLength=0）
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes != null) {
            if (((HeadBytes[0] & 0xff) - (SendBytes[0] & 0xff)) == 0x80) {
                return true;
            }
        }
        return false;
    }

}
