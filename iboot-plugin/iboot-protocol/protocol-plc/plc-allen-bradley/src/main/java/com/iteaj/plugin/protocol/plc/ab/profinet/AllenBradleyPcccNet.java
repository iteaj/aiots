package com.iteaj.plugin.protocol.plc.ab.profinet;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class AllenBradleyPcccNet extends NetworkConnectedCip {

    /**
     * 实例化一个默认的对象
     */
    public AllenBradleyPcccNet( ) {
        WordLength = 2;
        setByteTransform(new RegularByteTransform());
    }

    /**
     * 根据指定的IP及端口来实例化这个连接对象
     * @param ipAddress PLC的Ip地址
     * @param port PLC的端口号信息
     */
    public AllenBradleyPcccNet( String ipAddress, int port ) {
        this();
        setIpAddress(ipAddress);
        setPort(port);
    }

    protected byte[] GetLargeForwardOpen( short connectionID ) {
        TOConnectionId = new Random().nextInt();
        byte[] buffer = SoftBasic.HexStringToBytes("00 00 00 00 0a 00 02 00 00 00 00 00 b2 00 30 00 54 02 20 06 24 01 0a 05 00 00 00 00 e8 a3 14 00 27 04 09 10 0b 46 a5 c1 07 00 00 00 01 40 20 00 f4 43 01 40 20 00 f4 43 a3 03 01 00 20 02 24 01");
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(AllenBradleyHelper.OriginatorVendorID), buffer, 34);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(AllenBradleyHelper.OriginatorSerialNumber), buffer, 36);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(TOConnectionId), buffer, 28);
        return buffer;
    }

    protected byte[] GetLargeForwardClose( ) {
        return SoftBasic.HexStringToBytes("00 00 00 00 0a 00 02 00 00 00 00 00 b2 00 18 00 4e 02 20 06 24 01 0a 05 27 04 09 10 0b 46 a5 c1 03 00 01 00 20 02 24 01");
    }

    /**
     * @param address 读取PLC的原始数据信息，地址示例：N7:0
     * @param length 地址长度信息
     */
    // 读取PLC的原始数据信息，地址示例：N7:0
    public OperateResultExOne<byte[]> Read(String address, short length )
    {
        // 指令生成 -> Instruction Generation
        OperateResultExOne<byte[]> command = AllenBradleyHelper.PackExecutePCCCRead( (int)incrementCount.GetCurrentValue( ), address, length );
        if (!command.IsSuccess) return command;

        // 核心交互 -> Core Interactions
        OperateResultExOne<byte[]> read = ReadFromCoreServer( PackCommandService( command.Content ) );
        if (!read.IsSuccess) return read;

        // 检查反馈 -> Check Feedback
        OperateResult check = AllenBradleyHelper.CheckResponse( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 提取数据 -> Extracting data
        OperateResultExThree<byte[], Short, Boolean> extra = NetworkConnectedCip.ExtractActualData( read.Content, true );
        if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult( extra );

        return OperateResultExOne.CreateSuccessResult( extra.Content1 );
    }

    /**
     * @param address 写入PLC的原始数据信息，地址示例：N7:0
     */
    public OperateResult Write( String address, byte[] value )
    {
        // 指令生成 -> Instruction Generation
        OperateResultExOne<byte[]> command = AllenBradleyHelper.PackExecutePCCCWrite( (int)incrementCount.GetCurrentValue( ), address, value );
        if (!command.IsSuccess) return command;

        // 核心交互 -> Core Interactions
        OperateResultExOne<byte[]> read = ReadFromCoreServer( PackCommandService( command.Content ) );
        if (!read.IsSuccess) return read;

        // 检查反馈 -> Check Feedback
        OperateResult check = AllenBradleyHelper.CheckResponse( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 提取数据 -> Extracting data
        OperateResultExThree<byte[], Short, Boolean> extra = NetworkConnectedCip.ExtractActualData( read.Content, true );
        if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult( extra );

        return OperateResult.CreateSuccessResult( );
    }

    public OperateResultExOne<Boolean> ReadBool( String address )
    {
        int bitIndex = 0;
        OperateResultExTwo<String, Integer> analysis = AllenBradleySLCNet.AnalysisBitIndex( address );
        if (analysis.IsSuccess){
            address = analysis.Content1;
            bitIndex = analysis.Content2;
        }

        OperateResultExOne<byte[]> read = Read( address, (short) (bitIndex / 16 * 2 + 2 ));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        return OperateResultExOne.CreateSuccessResult( SoftBasic.ByteToBoolArray(read.Content )[bitIndex] );
    }

    public OperateResult Write( String address, boolean value )
    {
        int bitIndex = 0;
        OperateResultExTwo<String, Integer> analysis = AllenBradleySLCNet.AnalysisBitIndex( address );
        if (analysis.IsSuccess){
            address = analysis.Content1;
            bitIndex = analysis.Content2;
        }

        // 指令生成 -> Instruction Generation
        OperateResultExOne<byte[]> command = AllenBradleyHelper.PackExecutePCCCWrite( (int)incrementCount.GetCurrentValue( ), address, bitIndex, value );
        if (!command.IsSuccess) return command;

        // 核心交互 -> Core Interactions
        OperateResultExOne<byte[]> read = ReadFromCoreServer( PackCommandService( command.Content ) );
        if (!read.IsSuccess) return read;

        // 检查反馈 -> Check Feedback
        OperateResult check = AllenBradleyHelper.CheckResponse( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 提取数据 -> Extracting data
        OperateResultExThree<byte[], Short, Boolean> extra = NetworkConnectedCip.ExtractActualData( read.Content, true );
        if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult( extra );

        return OperateResult.CreateSuccessResult( );
    }

    public OperateResultExOne<String> ReadString( String address ) {
        return ReadString( address, (short) 0, StandardCharsets.US_ASCII);
    }

    /// <inheritdoc/>
    /// <remarks>
    /// 读取PLC的地址信息，如果输入了 ST 的地址，例如 ST10:2, 当长度指定为 0 的时候，这时候就是动态的读取PLC来获取实际的字符串长度。<br />
    /// Read the PLC address information, if the ST address is entered, such as ST10:2, when the length is specified as 0, then the PLC is dynamically read to obtain the actual string length.
    /// </remarks>
    public OperateResultExOne<String> ReadString( String address, short length, Charset encoding )
    {
        if ( !Utilities.IsStringNullOrEmpty( address ) && address.startsWith("ST") )
        {
            if ( length <= 0)
            {
                OperateResultExOne<byte[]> read = Read( address, (short) 2 );
                if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

                int len = getByteTransform().TransUInt16( read.Content, 0 );

                read = Read( address, (short) (2 + (len % 2 != 0 ? len + 1 : len)) );
                if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

                return OperateResultExOne.CreateSuccessResult( new String( SoftBasic.BytesReverseByWord( read.Content ), 2, len, encoding ) );
            }
            else
            {
                OperateResultExOne<byte[]> read = Read( address, (short) (length % 2 != 0 ? length + 3 : length + 2) );
                if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

                int len = getByteTransform().TransUInt16( read.Content, 0 );
                if (len + 2 > read.Content.length) len = read.Content.length - 2;

                return OperateResultExOne.CreateSuccessResult( new String( SoftBasic.BytesReverseByWord( read.Content ), 2, len, encoding ) );
            }
        }
        else
        {
            return super.ReadString( address, length, encoding );
        }
    }

    public OperateResult Write(String address, String value, Charset charset)
    {
        if (!Utilities.IsStringNullOrEmpty( address ) && address.startsWith( "ST" ))
        {
            byte[] temp = value.getBytes(charset);
            int len = temp.length;
            temp = SoftBasic.ArrayExpandToLengthEven( temp );

            return Write( address, SoftBasic.SpliceArray( new byte[] { BitConverter.GetBytes( len )[0], BitConverter.GetBytes( len )[1] }, SoftBasic.BytesReverseByWord( temp ) ) );
        }
        else
        {
            return super.Write( address, value, charset );
        }
    }

    public OperateResultExOne<Byte> ReadByte(String address ) {
        OperateResultExOne<byte[]> read = Read( address, (short) 1 );
        if (!read.IsSuccess) return  OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    public OperateResult Write(String address, byte value ) {
        return Write(address, new byte[] { value } );
    }


    private SoftIncrementCount incrementCount = new SoftIncrementCount( 65535, 2, 2 );

    public String toString( )
    {
        return "AllenBradleyPcccNet[" + getIpAddress() + ":" + getPort() + "]";
    }
}
