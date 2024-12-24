package com.iteaj.plugin.protocol.plc.core.message;

/**
 * Fuji的CommandSettingType的消息类
 */
public class FujiCommandSettingTypeMessage extends  NetMessageBase implements INetMessage
{
    public int ProtocolHeadBytesLength() {
        return 5;
    }

    public int GetContentLengthByHeadBytes(){
        if (getHeadBytes() == null) return 0;
        return getHeadBytes()[4] & 0xff;
    }
}
