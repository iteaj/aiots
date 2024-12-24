package com.iteaj.plugin.protocol.plc.core.profinet.Toyota;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.address.ToyoPucAddress;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.ToyoPucMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;
import com.iteaj.plugin.protocol.plc.core.types.BitConverter;
import com.iteaj.plugin.protocol.plc.core.types.Convert;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 * 丰田工机的计算机链接协议实现，通过2PORT-EFR模块实现对PLC的读写数据操作，需要在PLC配置相关的以太网端口信息，才能进行通信。具体的地址参考DEMO程序信息。<br />
 * Toyota Gongki's computer link protocol realizes the reading and writing data operation of the PLC through the 2PORT-EFR module,
 * and the relevant Ethernet port information needs to be configured in the PLC in order to communicate. For specific addresses, refer to Demo UI.<br /><br />
 *
 * 位地址支持  K,V,T,C,L,X,Y,M,EK,EV,EC,ET,EL,EX,EY,EM,GX,GY,GM 地址，也可以指定prg参数，例如 prg=1;K000<br />
 * 当然也可以使用字地址访问上述的位地址的，例如 M100~M10F 的位地址，就等于 M10 读字，也就是short。字地址额外支持：S,N,R,D,B,ES,EN,H,U,EB
 */
public class ToyoPuc extends NetworkDeviceBase {

    // region Constructor

    /**
     * 实例化一个默认的对象
     */
    public ToyoPuc( ) {
        this.setByteTransform(new RegularByteTransform());
        this.WordLength = 1;
    }

    /**
     * 指定IP地址，端口号信息来实例化一个对象
     * @param ipAddress IP地址
     * @param port 端口号
     */
    public ToyoPuc( String ipAddress, int port ) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    protected INetMessage GetNewNetMessage( ){
        return new ToyoPucMessage( );
    }

    // endregion

    // region Pack Unpack

    protected byte[] PackCommandWithHeader( byte[] command ) {
        byte[] buffer = new byte[command.length + 4];
        buffer[2] = BitConverter.GetBytes(command.length)[0];
        buffer[3] = BitConverter.GetBytes(command.length)[1];
        Utilities.ByteArrayCopyTo(command, buffer, 4);
        return buffer;
    }

    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response ) {
        if (response == null || response.length < 4) return new OperateResultExOne<byte[]>( "Receive data too short: " + SoftBasic.ByteToHexString(response, ' ' ) );
        if (response[0] != (byte) 0x80) return new OperateResultExOne<byte[]>( "FT check failed: " + SoftBasic.ByteToHexString(response, ' ' ) );
        if (response[1] != 0x00) return new OperateResultExOne<byte[]>( response.length == 4 ? GetErrorText( response[1] ) : GetErrorText( response[4] ) );

        if (response.length > 5)
            return OperateResultExOne.CreateSuccessResult( SoftBasic.BytesArrayRemoveBegin(response,5 ) );
        else
            return OperateResultExOne.CreateSuccessResult( new byte[0] );
    }

    private static String GetErrorText(byte code )
    {
        switch (code)
        {
            case 0x11: return StringResources.Language.ToyoPuc11();
            case 0x20: return StringResources.Language.ToyoPuc20();
            case 0x21: return StringResources.Language.ToyoPuc21();
            case 0x23: return StringResources.Language.ToyoPuc23();
            case 0x24: return StringResources.Language.ToyoPuc24();
            case 0x25: return StringResources.Language.ToyoPuc25();
            case 0x34: return StringResources.Language.ToyoPuc34();
            case 0x3E: return StringResources.Language.ToyoPuc3E();
            case 0x3F: return StringResources.Language.ToyoPuc3F();
            case 0x40: return StringResources.Language.ToyoPuc40();
            case 0x41: return StringResources.Language.ToyoPuc41();
            default: return StringResources.Language.UnknownError();
        }
    }

    // endregion

    // region ReadWrite

    private OperateResultExOne<WordAddress> ExtraWordAddress( String address, short length )
    {
        try
        {
            int bitIndex = 0;
            int index = address.indexOf( '.' );
            if (index > 0)
            {
                bitIndex = Convert.ToInt32( address.substring( index + 1 ), 16 );
                address = address.substring( 0, index );
            }
            else
            {
                // 可能只输入了一个地址信息
                bitIndex = Convert.ToInt32( address.substring( address.length() - 1 ), 16 );
                address = address.substring( 0, address.length() - 1 );
                if (address.startsWith( "EK" )||
                        address.startsWith( "EV" ) ||
                        address.startsWith( "ET" ) ||
                        address.startsWith( "EC" ) ||
                        address.startsWith( "EL" ) ||
                        address.startsWith( "EX" ) ||
                        address.startsWith( "EY" ) ||
                        address.startsWith( "EM" ))
                {
                    if (address.length() == 2) address = address + "0";
                }
                else
                {
                    if (address.length() == 1) address = address + "0";
                }

            }

            int readLength = (bitIndex + length + 15) / 16;
            WordAddress wordAddress = new WordAddress();
            wordAddress.Address = address;
            wordAddress.BitIndex = bitIndex;
            wordAddress.WordLength = readLength;
            return OperateResultExOne.CreateSuccessResult( wordAddress );
        }
        catch ( Exception ex)
        {
            return new OperateResultExOne<>( "ExtraWordAddress failed: " + ex.getMessage() );
        }
    }

    /**
     * @apiNote 地址类型地址 K,V,T,C,L,X,Y,M,EK,EV,EC,ET,EL,EX,EY,EM 地址，也可以指定程序号(prg)参数，例如 prg=1;K000
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        OperateResultExOne<WordAddress> analysis = ExtraWordAddress(address, length);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        // 提取字地址，位地址，读取长度信息
        OperateResultExOne<byte[]> read = Read(analysis.Content.Address, (short) analysis.Content.WordLength);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(read.Content), analysis.Content.BitIndex, length));
    }

    /**
     * @apiNote 地址类型地址 K,V,T,C,L,X,Y,M,EK,EV,EC,ET,EL,EX,EY,EM 地址，也可以指定程序号(prg)参数，例如 prg=1;K000
     */
    public OperateResult Write(String address, boolean value ) {
        OperateResultExOne<byte[]> command = BuildWriteBoolCommand(address, value);
        if (!command.IsSuccess) return command;

        return ReadFromCoreServer(command.Content);
    }

    /**
     * @apiNote 地址类型地址 K,V,T,C,L,X,Y,M,EK,EV,EC,ET,EL,EX,EY,EM,S,N,R,D,B,ES,EN,H,U,EB地址，也可以指定程序号(prg)参数，例如 prg=1;K000
     */
    public OperateResultExOne<byte[]> Read( String address, short length ) {
        OperateResultExOne<byte[]> command = BuildReadWordCommand(address, length);
        if (!command.IsSuccess) return command;

        return ReadFromCoreServer(command.Content);
    }

    /**
     * @apiNote 地址类型地址 K,V,T,C,L,X,Y,M,EK,EV,EC,ET,EL,EX,EY,EM,S,N,R,D,B,ES,EN,H,U,EB地址，也可以指定程序号(prg)参数，例如 prg=1;K000
     */
    public OperateResult Write(String address, byte[] value ) {
        OperateResultExOne<byte[]> command = BuildWriteWordCommand(address, value);
        if (!command.IsSuccess) return command;

        return ReadFromCoreServer(command.Content);
    }

    // endregion

    // region Object Override

    public String toString( ) {
        return  "ToyoPuc[" + getIpAddress() + ":" + getPort() + "]";
    }

    // endregion

    // region Static Helper

    private static OperateResultExOne<byte[]> BuildReadBoolCommand( String address )
    {
        OperateResultExOne<ToyoPucAddress> analysisAddress = ToyoPucAddress.ParseFrom( address, (short) 1, true );
        if (!analysisAddress.IsSuccess) return OperateResultExOne.CreateFailedResult( analysisAddress );

        ToyoPucAddress pucAddress = analysisAddress.Content;
        if (pucAddress.PRG >= 0)
        {
            return new OperateResultExOne<byte[]>( );
        }
        else
        {
            byte[] buffer = new byte[3];
            buffer[0] = 0x20;
            buffer[1] = BitConverter.GetBytes( pucAddress.getAddressStart() )[0];
            buffer[2] = BitConverter.GetBytes( pucAddress.getAddressStart() )[1];
            return OperateResultExOne.CreateSuccessResult( buffer );
        }
    }

    private static OperateResultExOne<byte[]> BuildReadWordCommand( String address, short length )
    {
        OperateResultExOne<ToyoPucAddress> analysisAddress = ToyoPucAddress.ParseFrom( address, length, false );
        if (!analysisAddress.IsSuccess) return OperateResultExOne.CreateFailedResult( analysisAddress );

        ToyoPucAddress pucAddress = analysisAddress.Content;
        if (pucAddress.PRG >= 0)
        {
            byte[] buffer = new byte[6];
            buffer[0] = (byte) 0x94;
            buffer[1] = (byte)pucAddress.PRG;
            buffer[2] = BitConverter.GetBytes( pucAddress.getAddressStart() )[0];
            buffer[3] = BitConverter.GetBytes( pucAddress.getAddressStart() )[1];
            buffer[4] = BitConverter.GetBytes( length )[0];
            buffer[5] = BitConverter.GetBytes( length )[1];
            return OperateResultExOne.CreateSuccessResult( buffer );
        }
        else
        {
            byte[] buffer = new byte[5];
            buffer[0] = 0x1C;
            buffer[1] = BitConverter.GetBytes( pucAddress.getAddressStart() )[0];
            buffer[2] = BitConverter.GetBytes( pucAddress.getAddressStart() )[1];
            buffer[3] = BitConverter.GetBytes( length )[0];
            buffer[4] = BitConverter.GetBytes( length )[1];
            return OperateResultExOne.CreateSuccessResult( buffer );
        }
    }

    private static OperateResultExOne<byte[]> BuildWriteWordCommand( String address, byte[] value )
    {
        OperateResultExOne<ToyoPucAddress> analysisAddress = ToyoPucAddress.ParseFrom( address, (short) 1, false );
        if (!analysisAddress.IsSuccess) return OperateResultExOne.CreateFailedResult( analysisAddress );

        ToyoPucAddress pucAddress = analysisAddress.Content;
        if (pucAddress.PRG >= 0)
        {
            byte[] buffer = new byte[4 + value.length];
            buffer[0] = (byte) 0x95;
            buffer[1] = (byte)pucAddress.PRG;
            buffer[2] = BitConverter.GetBytes( pucAddress.getAddressStart() )[0];
            buffer[3] = BitConverter.GetBytes( pucAddress.getAddressStart() )[1];
            Utilities.ByteArrayCopyTo(value, buffer, 4);
            return OperateResultExOne.CreateSuccessResult( buffer );
        }
        else
        {
            byte[] buffer = new byte[3 + value.length];
            buffer[0] = 0x1D;
            buffer[1] = BitConverter.GetBytes( pucAddress.getAddressStart() )[0];
            buffer[2] = BitConverter.GetBytes( pucAddress.getAddressStart() )[1];
            Utilities.ByteArrayCopyTo(value, buffer, 3);
            return OperateResultExOne.CreateSuccessResult( buffer );
        }
    }

    private static OperateResultExOne<byte[]> BuildWriteBoolCommand(String address, boolean value )
    {
        OperateResultExOne<ToyoPucAddress> analysisAddress = ToyoPucAddress.ParseFrom( address, (short) 1, true );
        if (!analysisAddress.IsSuccess) return OperateResultExOne.CreateFailedResult( analysisAddress );

        ToyoPucAddress pucAddress = analysisAddress.Content;
        if (pucAddress.PRG >= 0)
        {
            return new OperateResultExOne<byte[]>( "Not supported prg write bool" );
        }
        else
        {
            byte[] buffer = new byte[4];
            buffer[0] = 0x21;
            buffer[1] = BitConverter.GetBytes( pucAddress.getAddressStart() )[0];
            buffer[2] = BitConverter.GetBytes( pucAddress.getAddressStart() )[1];
            if (value) buffer[3] = 0x01;
            return OperateResultExOne.CreateSuccessResult( buffer );
        }
    }

    // endregion


}
