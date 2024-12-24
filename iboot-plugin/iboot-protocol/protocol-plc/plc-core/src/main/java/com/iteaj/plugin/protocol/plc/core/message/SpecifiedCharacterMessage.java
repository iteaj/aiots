package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.Utilities;

public class SpecifiedCharacterMessage extends NetMessageBase implements INetMessage
{
    /**
     * 使用固定的一个字符结尾作为当前的报文接收条件，来实例化一个对象<br />
     * Instantiate an object using a fixed end of one character as the current message reception condition
     * @param endCode 结尾的字符
     */
    public SpecifiedCharacterMessage(byte endCode){
        byte[] buffer = new byte[4];
        buffer[3] = (byte)(buffer[3] | 0x80);        // 最高位设置为1
        buffer[3] = (byte)(buffer[3] | 0x01);        // 表示只有一个字节结尾
        buffer[1] = endCode;

        protocolHeadBytesLength = Utilities.getInt( buffer, 0 );
    }

    public SpecifiedCharacterMessage( byte endCode1, byte endCode2){
        byte[] buffer = new byte[4];
        buffer[3] = (byte)(buffer[3] | 0x80);        // 最高位设置为1
        buffer[3] = (byte)(buffer[3] | 0x02);        // 表示有两个字节结尾
        buffer[1] = endCode1;
        buffer[0] = endCode2;

        protocolHeadBytesLength = Utilities.getInt( buffer, 0 );
    }

    /**
     * 获取在结束字符之后剩余的固定字节长度
     * @return 长度信息
     */
    public byte getEndLength(){
        return Utilities.getBytes( protocolHeadBytesLength )[2];
    }

    /**
     * 设置在结束字符之后剩余的固定字节长度，有些则还包含两个字节的校验码，这时该值就需要设置为2。<br />
     * Sets the remaining fixed byte length after the end character, and some also contain a two-byte check code. In this case, the value needs to be set to 2.
     * @param value 固定字节长度信息
     */
    public void setEndLength(byte value){
        byte[] buffer = Utilities.getBytes( protocolHeadBytesLength );
        buffer[2] = value;
        protocolHeadBytesLength = Utilities.getInt( buffer, 0 );
    }


    public int ProtocolHeadBytesLength(){
        return protocolHeadBytesLength;
    }

    public int GetContentLengthByHeadBytes(){
        return 0;
    }


    // region Private Member

    private int protocolHeadBytesLength = -1;

    // endregion
}
