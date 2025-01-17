package com.iteaj.plugin.protocol.plc.core.message;

import com.iteaj.plugin.protocol.plc.core.types.AsciiControl;
import com.iteaj.plugin.protocol.plc.core.types.MemoryStream;

public class RkcTemperatureMessage extends NetMessageBase implements INetMessage{

    public int ProtocolHeadBytesLength () {
        return -1;
    }

    public int GetContentLengthByHeadBytes( ) { return  0;}

    public boolean CheckReceiveDataComplete( byte[] send, MemoryStream ms )
    {
        byte[] buffer = ms.ToArray( );
        if (buffer.length == 1)
        {
            if (buffer[0] == AsciiControl.ACK || buffer[0] == AsciiControl.NAK) return true;   // 服务器接收客户端消息的情况
        }
        else if (buffer.length == 6 && buffer[0] == AsciiControl.EOT && buffer[buffer.length - 1] == AsciiControl.ENQ) return true;

        if (buffer.length > 3 && buffer[0] == AsciiControl.STX && buffer[buffer.length - 2] == AsciiControl.ETX) return true;
        if (buffer.length > 6 && buffer[0] == AsciiControl.EOT && buffer[3] == AsciiControl.STX && buffer[buffer.length - 2] == AsciiControl.ETX) return true;
        return false;
    }
}
