package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.BitConverter;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.net.Socket;
import java.util.ArrayList;

public class MelsecFxSerialOverTcp extends NetworkDeviceBase {


    /**
     * 实例化网络版的三菱的串口协议的通讯对象<br />
     * Instantiate the communication object of Mitsubishi's serial protocol on the network
     */
    public MelsecFxSerialOverTcp( ) {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.IsNewVersion = true;
        this.getByteTransform().setIsStringReverse(true);
        this.setSleepTime(20);
        this.incrementCount = new SoftIncrementCount(Integer.MAX_VALUE, 1);

        this.inis.add("00 10 02 FF FF FC 01 10 03");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002D001C091A18000000000000000000FC000012120414000113960AC4E5D7010201000000023030453032303203364310033543");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002D001C091A18000000000000000000FC000012120414000113960AC4E5D7010202000000023030454341303203384510033833");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002D001C091A18000000000000000000FC000012120414000113960AC4E5D7010203000000023030453032303203364310033545");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002D001C091A18000000000000000000FC000012120414000113960AC4E5D7010204000000023030454341303203384510033835");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102050000000245303138303030343003443510034342");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102060000000245303138303430314303453910034535");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102070000000245303030453030343003453110034436");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102080000000245303030453430343003453510034446");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D70102090000000245303030453830343003453910034538");
        this.inis.add("10025E000000FC00000000001212000000FFFF030000FF0300002F001C091A18000000000000000000FC000012120414000113960AC4E5D701020A0000000245303030454330343003463410034630");
    }

    /**
     * 指定ip地址及端口号来实例化三菱的串口协议的通讯对象<br />
     * Specify the IP address and port number to instantiate the communication object of Mitsubishi's serial protocol
     * @param ipAddress Ip地址
     * @param port 端口号
     */
    public MelsecFxSerialOverTcp( String ipAddress, int port )
    {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    /**
     * 获取是否使用GOT连接三菱的PLC，当使用了GOT连接到
     * @return 值
     */
    public boolean isUseGot() {
        return useGot;
    }

    /**
     * 设置是否使用GOT连接三菱的PLC，当使用了GOT连接到
     * @param useGot 是否使用的值
     */
    public void setUseGot(boolean useGot) {
        this.useGot = useGot;
    }

    private byte[] GetBytesSend( byte[] command )
    {
        ArrayList<Byte> array = new ArrayList<Byte>( );

        for (int i = 0; i < command.length; i++)
        {
            if (i < 2)
            {
                array.add( command[i] );
            }
            else if (i < command.length - 4)
            {
                if (command[i] == 0x10) array.add( command[i] );
                array.add( command[i] );
            }
            else
            {
                array.add( command[i] );
            }
        }
        return Utilities.ToByteArray(array);
    }

    private byte[] GetBytesReceive( byte[] response )
    {
        ArrayList<Byte> array = new ArrayList<>( );
        for (int i = 0; i < response.length; i++)
        {
            if (i < 2)
            {
                array.add( response[i] );
            }
            else if (i < response.length - 4)
            {
                if (response[i] == 0x10 && response[i + 1] == 0x10)
                {
                    array.add( response[i] );
                    i++;
                }
                else
                {
                    array.add( response[i] );
                }
            }
            else
            {
                array.add( response[i] );
            }
        }
        return Utilities.ToByteArray(array );
    }

    public byte[] PackCommandWithHeader( byte[] command )
    {
        if (useGot)
        {
            String[] ips = getIpAddress().split("\\.");

            byte[] buffer = new byte[32 + 34 + command.length];
            buffer[ 0] = 0x10;
            buffer[ 1] = 0x02;
            buffer[ 2] = 0x5E;
            buffer[ 6] = (byte) 0xFC;
            buffer[12] = 0x12;
            buffer[13] = 0x12;
            buffer[17] = (byte)0xFF;
            buffer[18] = (byte)0xFF;
            buffer[19] = 0x03;
            buffer[22] = (byte)0xFF;
            buffer[23] = 0x03;
            buffer[26] = BitConverter.GetBytes( 34 + command.length )[0];  // 报文长度信息 0x2D
            buffer[27] = BitConverter.GetBytes( 34 + command.length )[1];
            buffer[28] = 0x1C;
            buffer[29] = 0x09;
            buffer[30] = 0x1A;
            buffer[31] = 0x18;

            buffer[41] = (byte)0xFC;
            buffer[44] = 0x12;
            buffer[45] = 0x12;
            buffer[46] = 0x04;
            buffer[47] = 0x14;
            buffer[49] = 0x01;
            buffer[50] = BitConverter.GetBytes( getPort() )[1];                    // Port
            buffer[51] = BitConverter.GetBytes( getPort() )[0];
            buffer[52] = (byte) Integer.parseInt(ips[0]);             // IP
            buffer[53] = (byte) Integer.parseInt(ips[1]);;
            buffer[54] = (byte) Integer.parseInt(ips[2]);;
            buffer[55] = (byte) Integer.parseInt(ips[3]);;
            buffer[56] = 0x01;
            buffer[57] = 0x02;
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes( (int)incrementCount.GetCurrentValue( ) ), buffer, 58 );  // Message ID
            Utilities.ByteArrayCopyTo(command, buffer, 62 );
            buffer[buffer.length - 4] = 0x10;
            buffer[buffer.length - 3] = 0x03;
            Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC( buffer, 2, 4 ), buffer, buffer.length - 2 );       // CRC
            return GetBytesSend( buffer );
        }
        else
        {
            return super.PackCommandWithHeader( command );
        }
    }

    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response )
    {
        if (useGot)
        {
            if (response.length > 68)
            {
                response = GetBytesReceive( response );                     // 去除多余的0x10

                int index = -1;
                for (int i = 0; i < response.length - 4; i++)
                {
                    if (response[i] == 0x10 && response[i + 1] == 0x02)
                    {
                        index = i;
                        break;
                    }
                }

                if (index >= 0) return OperateResultExOne.CreateSuccessResult( SoftBasic.BytesArrayRemoveDouble(response, 64 + index, 4 ) );
            }
            return new OperateResultExOne<byte[]>( "Got failed: " + SoftBasic.ByteToHexString(response, ' ', 16 ) );
        }
        else
        {
            return super.UnpackResponseContent( send, response );
        }
    }

    private ArrayList<String> inis = new ArrayList<String>( );

    protected OperateResult InitializationOnConnect(Socket socket )
    {
        if (useGot)
        {
            for (int i = 0; i < inis.size(); i++)
            {
                OperateResult ini1 = ReadFromCoreServer( socket, SoftBasic.HexStringToBytes(inis.get(i) ),  true,  false );
                if (!ini1.IsSuccess) return ini1;
            }
        }
        return super.InitializationOnConnect( socket );
    }

    // 这部分的逻辑代码是失败或是读取的数据很短的话再读一次

    public OperateResultExOne<byte[]> ReadFromCoreServer( Socket socket, byte[] send, boolean hasResponseData, boolean usePackAndUnpack )
    {
        OperateResultExOne<byte[]> read = super.ReadFromCoreServer( socket, send, hasResponseData, usePackAndUnpack );
        if (!read.IsSuccess) return read;

        if (read.Content == null) return read;
        if (read.Content.length > 2) return read;

        OperateResultExOne<byte[]> read2 = super.ReadFromCoreServer( socket, send, hasResponseData, usePackAndUnpack );
        if (!read2.IsSuccess) return read2;

        return OperateResultExOne.CreateSuccessResult( SoftBasic.SpliceArray( read.Content, read2.Content ) );
    }

    /**
     * 当前的编程口协议是否为新版，默认为新版，如果无法读取，切换旧版再次尝试<br />
     * Whether the current programming port protocol is the new version, the default is the new version, if it cannot be read, switch to the old version and try again
     */
    public boolean IsNewVersion = false;

    public OperateResultExOne<byte[]> Read( String address, short length ) {
        return MelsecFxSerialHelper.Read(this, address, length, IsNewVersion);
    }

    public OperateResult Write( String address, byte[] value ) {
        return MelsecFxSerialHelper.Write(this, address, value, IsNewVersion);
    }

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        return MelsecFxSerialHelper.ReadBool(this, address, length, IsNewVersion);
    }

    public OperateResult Write( String address, boolean value ) {
        return MelsecFxSerialHelper.Write(this, address, value);
    }


    public OperateResult ActivePlc( ) {return MelsecFxSerialHelper.ActivePlc( this );}

    private boolean useGot = false;                 // 是否使用GOT连接的操作
    private SoftIncrementCount incrementCount;   // 自增的消息号信息

    public String toString( ){
        return  "MelsecFxSerialOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }

}
