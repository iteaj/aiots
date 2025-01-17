package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;

/**
 * Modbus Tcp协议的消息对象，用来确定接收规则的
 */
public class ModbusTcpMessage extends NetMessageBase implements INetMessage {
    public int ProtocolHeadBytesLength() {
        return 8;
    }

    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return 0;
        if (HeadBytes.length >= ProtocolHeadBytesLength()) {
            int length = (HeadBytes[4] & 0xff) * 256 + (HeadBytes[5] & 0xff);
            if (length == 0) {
                HeadBytes = SoftBasic.BytesArrayRemoveBegin(HeadBytes, 1);
                setHeadBytes(HeadBytes);
                return (HeadBytes[4] & 0xff) * 256 + (HeadBytes[5] & 0xff) - 1;
            } else {
                return Math.min(length - 2, 300);
            }
        }
        return 0;
    }

    @Override
    public boolean CheckHeadBytesLegal(byte[] token) {
        byte[] HeadBytes = getHeadBytes();
        byte[] SendBytes = getSendBytes();
        if (HeadBytes == null) return false;
        if (HeadBytes[2] == 0x00 && HeadBytes[3] == 0x00) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public int CheckMessageMatch(byte[] send, byte[] receive) {
        if (!IsCheckMessageId) return 1;
        if (send == null) return 1;
        if (receive == null) return 1;
        if (send.length < 8 || receive.length < 8) return 1;

        if (send[0] == receive[0] && send[1] == receive[1]) return 1;
        return -1;
    }

    /**
     * 获取或设置是否进行检查返回的消息ID和发送的消息ID是否一致，默认为true，也就是检查<br />
     * Get or set whether to check whether the returned message ID is consistent with the sent message ID, the default is true, that is, check
     */
    public boolean IsCheckMessageId = true;
}
