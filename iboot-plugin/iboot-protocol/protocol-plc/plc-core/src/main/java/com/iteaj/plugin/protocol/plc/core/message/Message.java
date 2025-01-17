package com.iteaj.plugin.protocol.plc.core.message;


import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.Utilities;

/**
 * 本组件系统使用的默认的消息规则，说明解析和反解析规则的
 */
public class Message extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 32;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();

        if (HeadBytes == null) return 0;
        if (HeadBytes.length != 32) return 0;
        return Utilities.getInt(HeadBytes, 28);
    }

    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        return SoftBasic.IsTwoBytesEquel(HeadBytes, 12, token, 0, 16);
    }

}
