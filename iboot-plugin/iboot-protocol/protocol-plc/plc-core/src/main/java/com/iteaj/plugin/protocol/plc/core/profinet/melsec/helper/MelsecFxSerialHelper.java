package com.iteaj.plugin.protocol.plc.core.profinet.melsec.helper;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.profinet.melsec.MelsecHelper;
import com.iteaj.plugin.protocol.plc.core.profinet.melsec.MelsecMcDataType;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MelsecFxSerialHelper {

    /**
     * 根据指定的地址及长度信息从三菱PLC中读取原始的字节数据，根据PLC中实际定义的规则，可以解析出任何类的数据信息<br />
     * Read the original byte data from the Mitsubishi PLC according to the specified address and length information.
     * According to the rules actually defined in the PLC, any type of data information can be parsed
     * @param plc PLC通信对象
     * @param address 读取地址，支持的类型参考文档说明
     * @param length 读取的数据长度
     * @param isNewVersion 是否是新版的串口访问类
     * @return 带成功标志的结果数据对象
     */
    public static OperateResultExOne<byte[]> Read(IReadWriteDevice plc, String address, short length, boolean isNewVersion )
    {
        // 获取指令
        OperateResultExOne<ArrayList<byte[]>> command = BuildReadWordCommand( address, length, isNewVersion );
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult( command );

        ArrayList<Byte> array = new ArrayList<>();
        for(int i = 0; i < command.Content.size(); i ++){
            // 核心交互
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer( command.Content.get(i) );
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

            // 反馈检查
            OperateResult ackResult = CheckPlcReadResponse( read.Content );
            if (!ackResult.IsSuccess) return OperateResultExOne.CreateFailedResult( ackResult );

            // 数据提炼
            OperateResultExOne<byte[]> extra = ExtractActualData( read.Content );
            if (!extra.IsSuccess) return  extra;

            Utilities.ArrayListAddArray(array, extra.Content);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(array));
    }

    /**
     * 从三菱PLC中批量读取位软元件，返回读取结果，该读取地址最好从0，16，32...等开始读取，这样可以读取比较长的数据数组<br />
     * Read bit devices in batches from Mitsubishi PLC and return the read results.
     * The read address should preferably be read from 0, 16, 32... etc., so that a relatively long data array can be read
     * @param plc PLC的通信对象
     * @param address 起始地址
     * @param length 读取的长度
     * @param isNewVersion 是否是新版的串口访问类
     * @return 带成功标志的结果数据对象
     */
    public static OperateResultExOne<boolean[]> ReadBool( IReadWriteDevice plc, String address, short length, boolean isNewVersion ) {
        //获取指令
        OperateResultExTwo<ArrayList<byte[]>, Integer> command = BuildReadBoolCommand(address, length, isNewVersion);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> array = new ArrayList<>();
        for (int i = 0; i < command.Content1.size(); i++) {
            // 核心交互
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(command.Content1.get(i));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 反馈检查
            OperateResult ackResult = CheckPlcReadResponse(read.Content);
            if (!ackResult.IsSuccess) return OperateResultExOne.CreateFailedResult(ackResult);

            // 提取真实的数据
            OperateResultExOne<byte[]> extra = ExtractActualData(read.Content);
            if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult(extra);
            ;

            Utilities.ArrayListAddArray(array, extra.Content);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(Utilities.ToByteArray(array)), command.Content2, length));
    }

    /**
     * 根据指定的地址向PLC写入数据，数据格式为原始的字节类型<br />
     * Write data to the PLC according to the specified address, the data format is the original byte type
     * @param plc PLC通信对象
     * @param address 初始地址，支持的类型参考文档说明
     * @param value 原始的字节数据
     * @param isNewVersion 是否是新版的串口访问类
     * @return 是否写入成功的结果对象
     */
    public static OperateResult Write( IReadWriteDevice plc, String address, byte[] value, boolean isNewVersion )
    {
        // 获取写入
        OperateResultExOne<byte[]> command = BuildWriteWordCommand( address, value, isNewVersion );
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer( command.Content );
        if (!read.IsSuccess) return read;

        // 结果验证
        return CheckPlcWriteResponse( read.Content );
    }

    /**
     * 强制写入位数据的通断，支持的类型参考文档说明<br />
     * The on-off of the forced write bit data, please refer to the document description for the supported types
     * @param plc PLC通信对象
     * @param address 地址信息
     * @param value 是否为通
     * @return 是否写入成功的结果对象
     */
    public static OperateResult Write( IReadWriteDevice plc, String address, boolean value )
    {
        // 先获取指令
        OperateResultExOne<byte[]> command = BuildWriteBoolPacket( address, value );
        if (!command.IsSuccess) return command;

        // 和串口进行核心的数据交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer( command.Content );
        if (!read.IsSuccess) return read;

        // 检查结果是否正确
        return CheckPlcWriteResponse( read.Content );
    }

    /**
     * 激活PLC的接收状态，需要再和PLC交互之前进行调用，之后就需要再调用了。<br />
     * @param plc PLC通信对象
     * @return 是否激活成功
     */
    public static OperateResult ActivePlc( IReadWriteDevice plc )
    {
        OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer( new byte[] { 0x05 } );
        if (!read1.IsSuccess) return read1;
        if (read1.Content[0] != 0x06) return new OperateResult( "Send 0x05, Check Receive 0x06 failed" );

        OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer( new byte[] { 0x02, 0x30, 0x30, 0x45, 0x30, 0x32, 0x30, 0x32, 0x03, 0x36, 0x43 } );
        if (!read2.IsSuccess) return read2;

        return plc.ReadFromCoreServer( new byte[] { 0x02, 0x30, 0x30, 0x45, 0x30, 0x32, 0x30, 0x32, 0x03, 0x36, 0x43 } );
    }

    /**
     * 检查PLC返回的读取数据是否是正常的
     * @param ack Plc反馈的数据信息
     * @return 检查结果
     */
    public static OperateResult CheckPlcReadResponse( byte[] ack )
    {
        if (ack.length == 0) return new OperateResult( StringResources.Language.MelsecFxReceiveZero() );
        if (ack[0] == AsciiControl.NAK) return new OperateResult( StringResources.Language.MelsecFxAckNagative() + " Actual: " + SoftBasic.ByteToHexString( ack, ' ' ) );
        if (ack[0] != AsciiControl.STX) return new OperateResult( StringResources.Language.MelsecFxAckWrong() + ack[0] + " Actual: " + SoftBasic.ByteToHexString( ack, ' ' ) );

        try
        {
            if (!MelsecHelper.CheckCRC( ack )) return new OperateResult( StringResources.Language.MelsecFxCrcCheckFailed ()+ " Actual: " + SoftBasic.ByteToHexString( ack, ' ' ) );
        }
        catch (Exception ex)
        {
            return new OperateResult( StringResources.Language.MelsecFxCrcCheckFailed() + ex.getMessage() + "\r\nActual: " + SoftBasic.ByteToHexString( ack, ' ' ) );
        }

        return OperateResult.CreateSuccessResult( );
    }

    /**
     * 检查PLC返回的写入的数据是否是正常的
     * @param ack Plc反馈的数据信息
     * @return 检查结果
     */
    public static OperateResult CheckPlcWriteResponse( byte[] ack )
    {
        if (ack.length == 0) return new OperateResult( StringResources.Language.MelsecFxReceiveZero() );
        if (ack[0] == AsciiControl.NAK) return new OperateResult( StringResources.Language.MelsecFxAckNagative() + " Actual: " + SoftBasic.ByteToHexString( ack, ' ' ) );
        if (ack[0] != AsciiControl.ACK) return new OperateResult( StringResources.Language.MelsecFxAckWrong() + ack[0] + " Actual: " + SoftBasic.ByteToHexString( ack, ' ' ) );

        return OperateResult.CreateSuccessResult( );
    }

    /**
     * 生成位写入的数据报文信息，该报文可直接用于发送串口给PLC
     * @param address 地址信息，每个地址存在一定的范围，需要谨慎传入数据。举例：M10,S10,X5,Y10,C10,T10
     * @param value <c>True</c>或是<c>False</c>
     * @return 带报文信息的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolPacket( String address, boolean value )
    {
        OperateResultExTwo<MelsecMcDataType, Integer> analysis = FxAnalysisAddress( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        // 此处使用的是一个特殊的写入方式，
        // 二次运算起始地址偏移量，根据类型的不同，地址的计算方式不同
        short startAddress = analysis.Content2.shortValue();
        if (analysis.Content1 == MelsecMcDataType.M)
        {
            if (startAddress >= 8000) startAddress = (short) (startAddress - 8000 + 0x0F00);
            else startAddress = (short) (startAddress + 0x0800);
        }
        else if (analysis.Content1 == MelsecMcDataType.S)  startAddress = (short)(startAddress + 0x0000);
        else if (analysis.Content1 == MelsecMcDataType.X)  startAddress = (short)(startAddress + 0x0400);
        else if (analysis.Content1 == MelsecMcDataType.Y)  startAddress = (short)(startAddress + 0x0500);
        else if (analysis.Content1 == MelsecMcDataType.CS) startAddress = (short)(startAddress + 0x01C0);
        else if (analysis.Content1 == MelsecMcDataType.CC) startAddress = (short)(startAddress + 0x03C0);
        else if (analysis.Content1 == MelsecMcDataType.CN) startAddress = (short)(startAddress + 0x0E00);
        else if (analysis.Content1 == MelsecMcDataType.TS) startAddress = (short)(startAddress + 0x00C0);
        else if (analysis.Content1 == MelsecMcDataType.TC) startAddress = (short)(startAddress + 0x02C0);
        else if (analysis.Content1 == MelsecMcDataType.TN) startAddress = (short)(startAddress + 0x0600);
        else return new OperateResultExOne<byte[]>( StringResources.Language.MelsecCurrentTypeNotSupportedBitOperate() );


        byte[] _PLCCommand = new byte[9];
        _PLCCommand[0] = 0x02;                                                       // STX
        _PLCCommand[1] = value ? (byte)0x37 : (byte)0x38;                            // 强制ON 或是 OFF
        _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom( startAddress )[2];           // 偏移地址
        _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom( startAddress )[3];
        _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom( startAddress )[0];
        _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom( startAddress )[1];
        _PLCCommand[6] = 0x03;                                                       // ETX
        Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC( _PLCCommand ), _PLCCommand, 7 );         // CRC

        return OperateResultExOne.CreateSuccessResult( _PLCCommand );
    }

    /**
     * 根据类型地址长度确认需要读取的指令头
     * @param address 起始地址
     * @param length 长度
     * @param isNewVersion 是否是新版的串口访问类
     * @return 带有成功标志的指令数据
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadWordCommand( String address, short length, boolean isNewVersion )
    {
        OperateResultExOne<Integer> addressResult = FxCalculateWordStartAddress( address, isNewVersion );
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult( addressResult );

        length = (short) (length * 2);
        short startAddress = addressResult.Content.shortValue();
        int[] splits = SoftBasic.SplitIntegerToArray( length, 254 );

        ArrayList<byte[]> array = new ArrayList<>( );
        for (int i = 0; i < splits.length; i++) {
            if (isNewVersion) {
                byte[] _PLCCommand = new byte[13];
                _PLCCommand[0] = AsciiControl.STX;                                      //报头
                _PLCCommand[1] = 0x45;                                                  //指令形式
                _PLCCommand[2] = 0x30;                                                  //Read
                _PLCCommand[3] = 0x30;                                                  //Read
                _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];      //起始地址
                _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];      //起始地址
                _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];      //起始地址
                _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];      //起始地址
                _PLCCommand[8] = SoftBasic.BuildAsciiBytesFrom((byte) length)[0];      // 读取长度
                _PLCCommand[9] = SoftBasic.BuildAsciiBytesFrom((byte) length)[1];
                _PLCCommand[10] = AsciiControl.ETX;
                Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, 11);   // CRC

                array.add(_PLCCommand);                // Return
                startAddress = (short) ( startAddress + splits[i]);
            } else {
                byte[] _PLCCommand = new byte[11];
                _PLCCommand[0] = AsciiControl.STX;                                        // STX
                _PLCCommand[1] = 0x30;                                                    // Read
                _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];        // 偏移地址
                _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];
                _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];
                _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];
                _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom((byte) length)[0];        // 读取长度
                _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom((byte) length)[1];
                _PLCCommand[8] = AsciiControl.ETX;                                        // ETX
                Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, 9);      // CRC

                array.add(_PLCCommand);                // Return
                startAddress = (short) ( startAddress + splits[i]);
            }
        }

        return OperateResultExOne.CreateSuccessResult( array );
    }

    /**
     * 根据类型地址长度确认需要读取的指令头
     * @param address 起始地址
     * @param length bool数组长度
     * @param isNewVersion 是否是新版的串口访问类
     * @return 带有成功标志的指令数据
     */
    public static OperateResultExTwo<ArrayList<byte[]>, Integer> BuildReadBoolCommand( String address, short length, boolean isNewVersion ) {
        OperateResultExThree<Integer, Integer, Integer> addressResult = FxCalculateBoolStartAddress(address, isNewVersion);
        if (!addressResult.IsSuccess) return OperateResultExTwo.CreateFailedResult(addressResult);

        // 计算下实际需要读取的数据长度
        short length2 = (short) ((addressResult.Content2 + length - 1) / 8 - (addressResult.Content2 / 8) + 1);
        short startAddress = addressResult.Content1.shortValue();

        int[] splits = SoftBasic.SplitIntegerToArray(length2, 254);
        ArrayList<byte[]> array = new ArrayList<>();
        for (int i = 0; i < splits.length; i++) {
            if (isNewVersion) {
                byte[] _PLCCommand = new byte[13];
                _PLCCommand[0] = AsciiControl.STX;                                      //报头
                _PLCCommand[1] = 0x45;                                                  //指令形式
                _PLCCommand[2] = 0x30;                                                  //Read
                _PLCCommand[3] = 0x30;                                                  //Read
                _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];      //起始地址
                _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];      //起始地址
                _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];      //起始地址
                _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];      //起始地址
                _PLCCommand[8] = SoftBasic.BuildAsciiBytesFrom((byte) length2)[0];      // 读取长度
                _PLCCommand[9] = SoftBasic.BuildAsciiBytesFrom((byte) length2)[1];
                _PLCCommand[10] = AsciiControl.ETX;
                Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, 11);   // CRC

                array.add(_PLCCommand);
            } else {
                byte[] _PLCCommand = new byte[11];
                _PLCCommand[0] = AsciiControl.STX;                                        // STX
                _PLCCommand[1] = 0x30;                                                    // Read
                _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom(startAddress)[0];        // 偏移地址
                _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom(startAddress)[1];
                _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom(startAddress)[2];
                _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom(startAddress)[3];
                _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom((byte) length2)[0];       // 读取长度
                _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom((byte) length2)[1];
                _PLCCommand[8] = AsciiControl.ETX;                                        // ETX
                Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC(_PLCCommand), _PLCCommand, 9);      // CRC

                array.add(_PLCCommand);
            }
        }

        return OperateResultExTwo.CreateSuccessResult(array, (int) addressResult.Content3);                // Return
    }

    /**
     * 根据类型地址以及需要写入的数据来生成指令头
     * @param address 起始地址
     * @param value 实际的数据信息
     * @param isNewVersion 是否是新版的串口访问类
     * @return 带有成功标志的指令数据
     */
    public static OperateResultExOne<byte[]> BuildWriteWordCommand( String address, byte[] value, boolean isNewVersion )
    {
        OperateResultExOne<Integer> addressResult = FxCalculateWordStartAddress( address, isNewVersion );
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult( addressResult );

        // 字节数据转换成ASCII格式
        if (value != null) value = SoftBasic.BuildAsciiBytesFrom( value );

        short startAddress = addressResult.Content.shortValue();

        if (isNewVersion)
        {
            byte[] _PLCCommand = new byte[13 + value.length];
            _PLCCommand[0] = AsciiControl.STX;                                                        //报头
            _PLCCommand[1] = 0x45;                                                                    //指令形式
            _PLCCommand[2] = 0x31;                                                                    //Write
            _PLCCommand[3] = 0x30;                                                                    //Write
            _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom( startAddress )[0];                        // Offect Address
            _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom( startAddress )[1];
            _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom( startAddress )[2];
            _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom( startAddress )[3];
            _PLCCommand[8] = SoftBasic.BuildAsciiBytesFrom( (byte)(value.length / 2) )[0];            // Read Length
            _PLCCommand[9] = SoftBasic.BuildAsciiBytesFrom( (byte)(value.length / 2) )[1];
            System.arraycopy( value, 0, _PLCCommand, 10, value.length );
            _PLCCommand[_PLCCommand.length - 3] = AsciiControl.ETX;                                   // ETX
            Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC( _PLCCommand ), _PLCCommand, _PLCCommand.length - 2 ); // CRC

            return OperateResultExOne.CreateSuccessResult( _PLCCommand );
        }
        else
        {
            byte[] _PLCCommand = new byte[11 + value.length];
            _PLCCommand[0] = AsciiControl.STX;              //报头
            _PLCCommand[1] = 0x31;                          //Write
            _PLCCommand[2] = SoftBasic.BuildAsciiBytesFrom( startAddress )[0];                        // Offect Address
            _PLCCommand[3] = SoftBasic.BuildAsciiBytesFrom( startAddress )[1];
            _PLCCommand[4] = SoftBasic.BuildAsciiBytesFrom( startAddress )[2];
            _PLCCommand[5] = SoftBasic.BuildAsciiBytesFrom( startAddress )[3];
            _PLCCommand[6] = SoftBasic.BuildAsciiBytesFrom( (byte)(value.length / 2) )[0];            // Read Length
            _PLCCommand[7] = SoftBasic.BuildAsciiBytesFrom( (byte)(value.length / 2) )[1];
            System.arraycopy( value, 0, _PLCCommand, 8, value.length );
            _PLCCommand[_PLCCommand.length - 3] = AsciiControl.ETX;                                   // ETX
            Utilities.ByteArrayCopyTo(MelsecHelper.FxCalculateCRC( _PLCCommand ), _PLCCommand, _PLCCommand.length - 2 ); // CRC

            return OperateResultExOne.CreateSuccessResult( _PLCCommand );
        }
    }

    /**
     * 从PLC反馈的数据进行提炼操作
     * @param response PLC反馈的真实数据
     * @return 数据提炼后的真实数据
     */
    public static OperateResultExOne<byte[]> ExtractActualData( byte[] response )
    {
        try
        {
            byte[] data = new byte[(response.length - 4) / 2];
            for (int i = 0; i < data.length; i++)
            {
                byte[] buffer = new byte[2];
                buffer[0] = response[i * 2 + 1];
                buffer[1] = response[i * 2 + 2];

                data[i] = (byte) Integer.parseInt( new String( buffer, StandardCharsets.US_ASCII), 16 );
            }

            return OperateResultExOne.CreateSuccessResult( data );
        }
        catch (Exception ex)
        {
            return new OperateResultExOne<byte[]>("Extract Msg：" + ex.getMessage() +
                    "\r\nData: " + SoftBasic.ByteToHexString( response ) );
        }
    }


    /**
     * 从PLC反馈的数据进行提炼bool数组操作
     * @param response PLC反馈的真实数据
     * @param start 起始提取的点信息
     * @param length bool数组的长度
     * @return 数据提炼后的真实数据
     */
    public static OperateResultExOne<boolean[]> ExtractActualBoolData( byte[] response, int start, int length )
    {
        OperateResultExOne<byte[]> extraResult = ExtractActualData( response );
        if (!extraResult.IsSuccess) return OperateResultExOne.CreateFailedResult( extraResult );

        // 转化bool数组
        try
        {
            boolean[] data = new boolean[length];
            boolean[] array = SoftBasic.ByteToBoolArray( extraResult.Content, extraResult.Content.length * 8 );
            for (int i = 0; i < length; i++)
            {
                data[i] = array[i + start];
            }

            return OperateResultExOne.CreateSuccessResult( data );
        }
        catch (Exception ex)
        {
            return new OperateResultExOne<boolean[]>("Extract Msg：" + ex.getMessage() +
                    "\r\nData: " + SoftBasic.ByteToHexString( response ) );
        }
    }

    /**
     * 解析数据地址成不同的三菱地址类型
     * @param address 数据地址
     * @return 地址结果对象
     */
    private static OperateResultExTwo<MelsecMcDataType, Integer> FxAnalysisAddress(String address )
    {
        OperateResultExTwo<MelsecMcDataType, Integer> result = new OperateResultExTwo<MelsecMcDataType, Integer>( );
        try
        {
            switch (address.charAt(0))
            {
                case 'M':
                case 'm':
                {
                    result.Content1 = MelsecMcDataType.M;
                    result.Content2 = Integer.parseInt( address.substring( 1 ), MelsecMcDataType.M.getFromBase() );
                    break;
                }
                case 'X':
                case 'x':
                {
                    result.Content1 = MelsecMcDataType.X;
                    result.Content2 = Integer.parseInt( address.substring( 1 ), 8 );
                    break;
                }
                case 'Y':
                case 'y':
                {
                    result.Content1 = MelsecMcDataType.Y;
                    result.Content2 = Integer.parseInt( address.substring( 1 ), 8 );
                    break;
                }
                case 'D':
                case 'd':
                {
                    result.Content1 = MelsecMcDataType.D;
                    result.Content2 = Integer.parseInt( address.substring( 1 ), MelsecMcDataType.D.getFromBase() );
                    break;
                }
                case 'S':
                case 's':
                {
                    result.Content1 = MelsecMcDataType.S;
                    result.Content2 = Integer.parseInt( address.substring( 1 ), MelsecMcDataType.S.getFromBase() );
                    break;
                }
                case 'T':
                case 't':
                {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n')
                    {
                        result.Content1 = MelsecMcDataType.TN;
                        result.Content2 = Integer.parseInt( address.substring( 2 ), MelsecMcDataType.TN.getFromBase() );
                        break;
                    }
                    else if (address.charAt(1) == 'S' || address.charAt(1) == 's')
                    {
                        result.Content1 = MelsecMcDataType.TS;
                        result.Content2 = Integer.parseInt( address.substring( 2 ), MelsecMcDataType.TS.getFromBase() );
                        break;
                    }
                    else if (address.charAt(1) == 'C' || address.charAt(1) == 'c')
                    {
                        result.Content1 = MelsecMcDataType.TC;
                        result.Content2 = Integer.parseInt( address.substring( 2 ), MelsecMcDataType.TC.getFromBase() );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                case 'C':
                case 'c':
                {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n')
                    {
                        result.Content1 = MelsecMcDataType.CN;
                        result.Content2 = Integer.parseInt( address.substring( 2 ), MelsecMcDataType.CN.getFromBase() );
                        break;
                    }
                    else if (address.charAt(1) == 'S' || address.charAt(1) == 's')
                    {
                        result.Content1 = MelsecMcDataType.CS;
                        result.Content2 = Integer.parseInt( address.substring( 2 ), MelsecMcDataType.CS.getFromBase() );
                        break;
                    }
                    else if (address.charAt(1) == 'C' || address.charAt(1) == 'c')
                    {
                        result.Content1 = MelsecMcDataType.CC;
                        result.Content2 = Integer.parseInt( address.substring( 2 ), MelsecMcDataType.CC.getFromBase() );
                        break;
                    }
                    else
                    {
                        throw new Exception( StringResources.Language.NotSupportedDataType() );
                    }
                }
                default: throw new Exception( StringResources.Language.NotSupportedDataType() );
            }
        }
        catch (Exception ex)
        {
            result.Message = ex.getMessage();
            return result;
        }

        result.IsSuccess = true;
        return result;
    }

    /**
     * 返回读取的地址及长度信息
     * @param address 读取的地址信息
     * @param isNewVersion 是否是新版的串口访问类
     * @return 带起始地址的结果对象
     */
    private static OperateResultExOne<Integer> FxCalculateWordStartAddress( String address, boolean isNewVersion )
    {
        // 初步解析，失败就返回
        OperateResultExTwo<MelsecMcDataType, Integer> analysis = FxAnalysisAddress( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        // 二次解析
        int startAddress = analysis.Content2;
        if (analysis.Content1 == MelsecMcDataType.D)
        {
            if (startAddress >= 8000) startAddress = (short) ((startAddress - 8000) * 2 + (isNewVersion ? 0x8000 : 0x0E00));
            else startAddress = isNewVersion ? (short)(startAddress * 2 + 0x4000) : (short)(startAddress * 2 + 0x1000);
        }
        else if (analysis.Content1 == MelsecMcDataType.CN)
        {
            if (startAddress >= 200) startAddress = (short)((startAddress - 200) * 4 + 0x0C00);
            else startAddress = (short)(startAddress * 2 + 0x0A00);
        }
        else if (analysis.Content1 == MelsecMcDataType.TN)
        {
            if (isNewVersion)
            {
                startAddress = (short)(startAddress * 2 + 0x1000);
            }
            else
            {
                startAddress = (short)(startAddress * 2 + 0x0800);
            }
        }
        else return new OperateResultExOne<>( StringResources.Language.MelsecCurrentTypeNotSupportedWordOperate ());

        return OperateResultExOne.CreateSuccessResult( startAddress );
    }

    /**
     * 返回读取的地址及长度信息，以及当前的偏置信息
     * @param address 读取的地址信息
     * @param isNewVersion 是否是新版的串口访问类
     * @return 带起始地址的结果对象
     */
    private static OperateResultExThree<Integer, Integer, Integer> FxCalculateBoolStartAddress(String address, boolean isNewVersion )
    {
        // 初步解析
        OperateResultExTwo<MelsecMcDataType, Integer> analysis = FxAnalysisAddress( address );
        if (!analysis.IsSuccess) return OperateResultExThree.CreateFailedResult( analysis );

        // 二次解析
        int startAddress = analysis.Content2;
        if (analysis.Content1 == MelsecMcDataType.M)
        {
            if (isNewVersion)
            {
                if (startAddress >= 8000) startAddress = (int)((startAddress - 8000) / 8 + 0x8C00);
                else startAddress = (int)(startAddress / 8 + 0x8800);
            }
            else
            {
                if (startAddress >= 8000) startAddress = (int)((startAddress - 8000) / 8 + 0x01E0);
                else startAddress = (int)(startAddress / 8 + 0x0100);
            }
        }
        else if (analysis.Content1 == MelsecMcDataType.X) startAddress  = (int)(startAddress / 8 + (isNewVersion ? 0x8CA0 : 0x0080));
        else if (analysis.Content1 == MelsecMcDataType.Y) startAddress  = (int)(startAddress / 8 + (isNewVersion ? 0x8BC0 : 0x00A0));
        else if (analysis.Content1 == MelsecMcDataType.S) startAddress  = (int)(startAddress / 8 + (isNewVersion ? 0x8CE0 : 0x0000));
        else if (analysis.Content1 == MelsecMcDataType.CS) startAddress = (int)(startAddress / 8 + (isNewVersion ? 0x9340 : 0x01C0));
        else if (analysis.Content1 == MelsecMcDataType.CC) startAddress = (int)(startAddress / 8 + (isNewVersion ? 0x92E0 : 0x03C0));
        else if (analysis.Content1 == MelsecMcDataType.TS) startAddress = (int)(startAddress / 8 + (isNewVersion ? 0x9360 : 0x00C0));
        else if (analysis.Content1 == MelsecMcDataType.TC) startAddress = (int)(startAddress / 8 + (isNewVersion ? 0x9300 : 0x02C0));
        else return new OperateResultExThree<Integer, Integer, Integer>( StringResources.Language.MelsecCurrentTypeNotSupportedBitOperate() );

        return OperateResultExThree.CreateSuccessResult( startAddress, analysis.Content2, (analysis.Content2 % 8) );
    }

}
