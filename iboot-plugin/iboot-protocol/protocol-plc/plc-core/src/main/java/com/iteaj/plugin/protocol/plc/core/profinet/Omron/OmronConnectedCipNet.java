package com.iteaj.plugin.protocol.plc.core.profinet.Omron;


import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.message.AllenBradleyMessage;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.ByteTransformHelper;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.profinet.AllenBradley.AllenBradleyHelper;
import com.iteaj.plugin.protocol.plc.core.profinet.AllenBradley.IReadWriteCip;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

public class OmronConnectedCipNet extends NetworkDeviceBase implements IReadWriteCip {
    /**
     * 实例化一个默认的对象
     */
    public OmronConnectedCipNet( )
    {
        WordLength    = 2;
       setByteTransform( new RegularByteTransform( ));
    }

    /**
     * 根据指定的IP及端口来实例化这个连接对象，端口默认是44818
     * @param ipAddress PLC的Ip地址
     * @param port PLC的端口号信息，端口默认是44818
     */
    public OmronConnectedCipNet(String ipAddress, int port )
    {
        this();
        setIpAddress(ipAddress);
        setPort(port);
    }

    protected INetMessage GetNewNetMessage( ) {
        return new AllenBradleyMessage( );
    }

    protected byte[] PackCommandWithHeader( byte[] command ) {
        return AllenBradleyHelper.PackRequestHeader(0x70, SessionHandle, AllenBradleyHelper.PackCommandSpecificData(
                GetOTConnectionIdService(), command));
    }

    protected OperateResult InitializationOnConnect(Socket socket ) {
        // Registering Session Information
        OperateResultExOne<byte[]> read1 = ReadFromCoreServer(socket, AllenBradleyHelper.RegisterSessionHandle(), true, false);
        if (!read1.IsSuccess) return read1;

        // Check the returned status
        OperateResult check = AllenBradleyHelper.CheckResponse(read1.Content);
        if (!check.IsSuccess) return check;

        // Extract session ID
        SessionHandle = getByteTransform().TransInt32(read1.Content, 4);

        // Open forward 10 times
        for (int i = 0; i < 10; i++) {
            short tick = i < 7 ? (short) i : (short) (100 + i);
            // Large Forward Open(Message Router)
            OperateResultExOne<byte[]> read2 = ReadFromCoreServer(socket, AllenBradleyHelper.PackRequestHeader(0x6f, SessionHandle, GetLargeForwardOpen(tick)), true, false);
            if (!read2.IsSuccess) return read2;

            try
            {
                if (read2.Content.length >= 46 && read2.Content[42] != 0x00)
                {
                    int err = getByteTransform().TransUInt16( read2.Content, 44 );
                    if (err == 0x100 && i < 9) continue;

                    if      (err == 0x100) return new OperateResult( "Connection in use or duplicate Forward Open" );
                    else if (err == 0x113) return new OperateResult( "Extended Status: Out of connections (0x0113)" );
                    return new OperateResult( "Forward Open failed, Code: " + getByteTransform().TransUInt16( read2.Content, 44 ) );
                }
                else
                {
                    // Extract Connection ID
                    OTConnectionId = getByteTransform().TransInt32( read2.Content, 44 );
                    break;
                }
            }
            catch (Exception ex)
            {
                return new OperateResult( ex.getMessage() + "\r\nSource: " + SoftBasic.ByteToHexString(read2.Content, ' ' ) );
            }
        }

        // Reset Message Id
        incrementCount.ResetCurrentValue();

        OperateResultExOne<byte[]> read3 = ReadFromCoreServer(socket, AllenBradleyHelper.PackRequestHeader(0x6f, SessionHandle, GetAttributeAll()), true, false);
        if (!read3.IsSuccess) return read3;

        if (read3.Content.length > 59)
            ProductName = new String(read3.Content, 59, read3.Content[58], StandardCharsets.UTF_8);
        return OperateResult.CreateSuccessResult();
    }

    protected OperateResult ExtraOnDisconnect( Socket socket ) {
        // Unregister session Information
        OperateResultExOne<byte[]> read = ReadFromCoreServer(socket, AllenBradleyHelper.UnRegisterSessionHandle(SessionHandle), true, false);
        if (!read.IsSuccess) return read;

        return OperateResult.CreateSuccessResult();
    }

    private int SessionHandle = 0;

    /**
     * 当前产品的型号信息<br />
     * Model information of the current product
     */
    public String ProductName = "";

    /**
     * O -> T Network Connection ID
     */
    private int OTConnectionId = 0;
    public byte ConnectionTimeoutMultiplier = 2;

    private byte[] GetOTConnectionIdService( )
    {
        byte[] buffer = new byte[8];
        buffer[0] = (byte) 0xA1;  // Connected Address Item
        buffer[1] = 0x00;
        buffer[2] = 0x04;  // Length
        buffer[3] = 0x00;
        System.arraycopy(getByteTransform().TransByte( OTConnectionId ), 0, buffer, 4, 4);
        return buffer;
    }

    private OperateResultExOne<byte[]> BuildReadCommand(String[] address, short[] length ) {
        try {
            ArrayList<byte[]> cips = new ArrayList<byte[]>();
            for (int i = 0; i < address.length; i++) {
                cips.add(AllenBradleyHelper.PackRequsetRead(address[i], length[i], true));
            }
            return OperateResultExOne.CreateSuccessResult(PackCommandService(Utilities.ToArray(cips)));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    private OperateResultExOne<byte[]> BuildWriteCommand( String address, short typeCode, byte[] data, int length ) {
        try {
            return OperateResultExOne.CreateSuccessResult(PackCommandService(AllenBradleyHelper.PackRequestWrite(address, typeCode, data, length, true)));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Address Wrong:" + ex.getMessage());
        }
    }

    private byte[] PackCommandService( byte[]... cip ) {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        // type id   0xB2:UnConnected Data Item  0xB1:Connected Data Item  0xA1:Connect Address Item
        ms.write(0xB1);
        ms.write(0x00);
        ms.write(0x00);     // 后续数据的长度
        ms.write(0x00);

        long messageId = incrementCount.GetCurrentValue();
        ms.write(Utilities.getBytes(messageId)[0]);     // CIP Sequence Count 一个累加的CIP序号
        ms.write(Utilities.getBytes(messageId)[1]);

        if (cip.length == 1) {
            ms.write(cip[0], 0, cip[0].length);
        } else {
            ms.write(new byte[]{0x0A, 0x02, 0x20, 0x02, 0x24, 0x01}, 0, 6);
            ms.write(Utilities.getBytes(cip.length)[0]);
            ms.write(Utilities.getBytes(cip.length)[1]);
            int offset = 2 + cip.length * 2;
            for (int i = 0; i < cip.length; i++) {
                ms.write(Utilities.getBytes(offset)[0]);     // 各个数据的长度
                ms.write(Utilities.getBytes(offset)[1]);
                offset += cip[i].length;
            }
            for (int i = 0; i < cip.length; i++) {
                ms.write(cip[i], 0, cip[i].length);     // 写入欧姆龙CIP的具体内容
            }
        }

        byte[] data = ms.toByteArray();
        System.arraycopy(Utilities.getBytes((short) (data.length - 4)), 0, data, 2, 2);
        return data;
    }

    private OperateResultExThree<byte[], Short, Boolean> ReadWithType(String[] address, short[] length )
    {
        // 指令生成 -> Instruction Generation
        OperateResultExOne<byte[]> command = BuildReadCommand( address, length );
        if (!command.IsSuccess) return OperateResultExThree.CreateFailedResult( command ); ;

        // 核心交互 -> Core Interactions
        OperateResultExOne<byte[]> read = ReadFromCoreServer( command.Content );
        if (!read.IsSuccess) return OperateResultExThree.CreateFailedResult( read ); ;

        // 检查反馈 -> Check Feedback
        OperateResult check = AllenBradleyHelper.CheckResponse( read.Content );
        if (!check.IsSuccess) return OperateResultExThree.CreateFailedResult( check );

        // 提取数据 -> Extracting data
        return ExtractActualData( read.Content, true );
    }

    public OperateResultExOne<byte[]> ReadCipFromServer( byte[]... cips ) {
        byte[] command = PackCommandService(cips);

        // 核心交互 -> Core Interactions
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command);
        if (!read.IsSuccess) return read;

        // 检查反馈 -> Check Feedback
        OperateResult check = AllenBradleyHelper.CheckResponse(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        return OperateResultExOne.CreateSuccessResult(read.Content);
    }

    public OperateResultExOne<byte[]> Read( String address, short length ) {
        OperateResultExThree<byte[], Short, Boolean> read = ReadWithType(new String[]{address}, new short[]{length});
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content1);
    }

    public OperateResultExOne<byte[]> Read( String[] address, short[] length ) {

        OperateResultExThree<byte[], Short, Boolean> read = ReadWithType(address, length);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content1);
    }

    /**
     * 读取bool数据信息，如果读取的是单bool变量，就直接写变量名，如果是 bool 数组，就 <br />
     * Read a single bool data information, if it is a single bool variable, write the variable name directly,
     * if it is a value of a bool array composed of int, it is always accessed with "i=" at the beginning, for example, "i=A[0]"
     * @param address 节点的名称 -> Name of the node
     * @param length 读取的数组长度信息
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        if (length == 1 && !Pattern.matches("\\[[0-9]+\\]$", address)) {
            OperateResultExOne<byte[]> read = Read(address, length);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray(read.Content));
        } else {
            OperateResultExOne<byte[]> read = Read(address, length);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            boolean[] values = new boolean[length];
            for (int i = 0; i < values.length; i++) {
                if (read.Content[i] != 0)
                    values[i] = true;
            }
            return OperateResultExOne.CreateSuccessResult(values);
        }
    }

    /**
     * 读取PLC的byte类型的数据<br />
     * Read the byte type of PLC data
     * @param address 节点的名称 -> Name of the node
     * @return 带有结果对象的结果数据 -> Result data with result info
     */
    public OperateResultExOne<Byte> ReadByte( String address ) {
        OperateResultExOne<byte[]> read = Read(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExTwo<Short, byte[]> ReadTag(String address, int length )
    {
        OperateResultExThree<byte[], Short, Boolean> read = ReadWithType( new String[] { address }, new short[] { (short) length } );
        if (!read.IsSuccess) return OperateResultExTwo.CreateFailedResult( read );

        return OperateResultExTwo.CreateSuccessResult( read.Content2, read.Content1 );
    }

    /**
     * 当前的PLC不支持该功能，需要调用 <see cref="WriteTag(string, ushort, byte[], int)"/> 方法来实现。<br />
     * The current PLC does not support this function, you need to call the <see cref = "WriteTag (string, ushort, byte [], int)" /> method to achieve it.
     * @param address 地址
     * @param value 值
     * @return 写入结果值
     */
    public OperateResult Write( String address, byte[] value ){
        return new OperateResult( StringResources.Language.NotSupportedFunction() + " Please refer to use WriteTag instead " );
    }

    public OperateResult WriteTag( String address, short typeCode, byte[] value ) {
        return WriteTag(address, typeCode, value, 1);
    }

    /**
     * 使用指定的类型写入指定的节点数据<br />
     * Writes the specified node data with the specified type
     * @param address 节点的名称 -> Name of the node
     * @param typeCode 类型代码，详细参见 {@link AllenBradleyHelper} 上的常用字段
     * @param value 实际的数据值 -> The actual data value
     * @param length 如果节点是数组，就是数组长度 -> If the node is an array, it is the array length
     * @return 是否写入成功 -> Whether to write successfully
     */
    public OperateResult WriteTag( String address, short typeCode, byte[] value, int length ) {
        OperateResultExOne<byte[]> command = BuildWriteCommand(address, typeCode, value, length);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        OperateResult check = AllenBradleyHelper.CheckResponse(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        return AllenBradleyHelper.ExtractActualData(read.Content, false);
    }


    public OperateResultExOne<short[]> ReadInt16( String address, short length ) {
        return ByteTransformHelper.GetResultFromBytes( Read( address, length ), new FunctionOperateExOne<byte[], short[]>() {
                    @Override
                    public short[] Action(byte[] content) {
                        return getByteTransform().TransInt16(content, 0, length);
                    }
                } );
    }

    public OperateResultExOne<int[]> ReadUInt16( String address, short length ){
        return ByteTransformHelper.GetResultFromBytes( Read( address, length ), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransUInt16(content, 0, length);
            }
        } );
    }

    public OperateResultExOne<int[]> ReadInt32( String address, short length ){
        return ByteTransformHelper.GetResultFromBytes( Read( address, length ), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransInt32(content, 0, length);
            }
        } );
    }

    public OperateResultExOne<long[]> ReadUInt32( String address, short length ) {
        return ByteTransformHelper.GetResultFromBytes( Read( address, length ), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransUInt32(content, 0, length);
            }
        } );
    }

    public OperateResultExOne<float[]> ReadFloat( String address, short length ) {
        return ByteTransformHelper.GetResultFromBytes( Read( address, length ), new FunctionOperateExOne<byte[], float[]>() {
            @Override
            public float[] Action(byte[] content) {
                return getByteTransform().TransSingle(content, 0, length);
            }
        } );
    }

    public OperateResultExOne<long[]> ReadInt64( String address, short length ) {
        return  ByteTransformHelper.GetResultFromBytes( Read( address, length ), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransInt64(content, 0, length);
            }
        } );
    }

    public OperateResultExOne<double[]> ReadDouble( String address, short length ) {
        return  ByteTransformHelper.GetResultFromBytes( Read( address, length ), new FunctionOperateExOne<byte[], double[]>() {
            @Override
            public double[] Action(byte[] content) {
                return getByteTransform().TransDouble(content, 0, length);
            }
        } );
    }

    public OperateResultExOne<String> ReadString( String address ) {
        return ReadString( address, (short) 1, StandardCharsets.UTF_8 );
    }

    /**
     * 读取字符串数据，默认为UTF-8编码<br />
     * Read string data, default is UTF-8 encoding
     * @param address 起始地址
     * @param length 数据长度
     * @return 带有成功标识的string数据
     */
    public OperateResultExOne<String> ReadString( String address, short length ) {
        return ReadString( address, length, StandardCharsets.UTF_8 );
    }

    public OperateResultExOne<String> ReadString( String address, short length, Charset encoding ) {
        OperateResultExOne<byte[]> read = Read(address, length);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        if (read.Content.length >= 2) {
            int strLength = getByteTransform().TransUInt16(read.Content, 0);
            return OperateResultExOne.CreateSuccessResult(new String(read.Content, 2, strLength, encoding));
        } else {
            return OperateResultExOne.CreateSuccessResult(new String(read.Content, encoding));
        }
    }


    public OperateResult Write( String address, short[] values ) {
        return  WriteTag( address, (short) AllenBradleyHelper.CIP_Type_Word, getByteTransform().TransByte( values ), values.length );
    }

    public OperateResult Write( String address, int[] values ) {
        return  WriteTag( address, (short) AllenBradleyHelper.CIP_Type_DWord, getByteTransform().TransByte( values ), values.length );
    }

    public OperateResult Write( String address, float[] values ) {
        return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_Real, getByteTransform().TransByte( values ), values.length );
    }

    public OperateResult Write( String address, long[] values ){
        return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_LInt, getByteTransform().TransByte( values ), values.length );
    }

    public OperateResult Write( String address, double[] values ) {
        return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_Double, getByteTransform().TransByte( values ), values.length );
    }

    public OperateResult Write( String address, String value ) {
        return Write(address, value, StandardCharsets.UTF_8);
    }

    public OperateResult Write( String address, String value, Charset encoding ) {
        byte[] buffer = Utilities.IsStringNullOrEmpty(value) ? new byte[0] : value.getBytes(encoding);
        byte[] result = new byte[buffer.length + 2];
        System.arraycopy(Utilities.getBytes((short) buffer.length), 0, result, 0, 2);
        if (buffer.length > 0) System.arraycopy(buffer, 0, result, 2, buffer.length);
        return WriteTag(address, (short) AllenBradleyHelper.CIP_Type_String, result);
    }

    public OperateResult Write( String address, boolean value ) {
		return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_Bool, value ? new byte[] { (byte) 0xFF, (byte) 0xFF } : new byte[] { 0x00, 0x00 } );
    }

    public OperateResult Write( String address, byte value ) {
        return WriteTag( address, (short) AllenBradleyHelper.CIP_Type_Byte, new byte[] { value } );
    }

    // public OperateResult Write( string address, sbyte value ) => WriteTag( address, AllenBradleyHelper.CIP_Type_USInt, new byte[] { (byte)value } );

    private SoftIncrementCount incrementCount = new SoftIncrementCount( 65535 );
    private Random random = new Random( );

    private byte[] GetLargeForwardOpen(short connectionID ) {
        byte[] buffer = SoftBasic.HexStringToBytes("00 00 00 00 00 00 02 00 00 00 00 00 b2 00 34 00 5b 02 20 06 24 01 06 9c 02 00 00 80 01 00 fe 80" +
                "02 00 1b 05 30 a7 2b 03 02 00 00 00 80 84 1e 00 cc 07 00 42 80 84 1e 00 cc 07 00 42 a3 03 20 02 24 01 2c 01");
        buffer[40] = ConnectionTimeoutMultiplier;
        System.arraycopy(BitConverter.GetBytes((short) (2 + connectionID)), 0, buffer, 32, 2);   // Connection Serial Number
        return buffer;
    }

    private byte[] GetAttributeAll( )
    {
        return SoftBasic.HexStringToBytes("00 00 00 00 00 00 02 00 00 00 00 00 b2 00 06 00 01 02 20 01 24 01" );
    }


    public OperateResultExOne<String> ReadPlcType( ) {
        return OperateResultExOne.CreateSuccessResult(ProductName);
    }

    //region Date ReadWrite

    public OperateResultExOne<Date> ReadDate(String address ) {
        return AllenBradleyHelper.ReadDate( this, address );
    }

    public OperateResult WriteDate( String address, Date date ) {
        return AllenBradleyHelper.WriteDate( this, address, date );
    }

    public OperateResult WriteTimeAndDate( String address, Date date ) {
        return AllenBradleyHelper.WriteTimeAndDate( this, address, date );
    }

    public OperateResultExOne<Duration> ReadTime(String address ) {
        return AllenBradleyHelper.ReadTime( this, address );
    }

    public OperateResult WriteTime( String address, Duration time ) {
        return AllenBradleyHelper.WriteTime( this, address, time );
    }

    public OperateResult WriteTimeOfDate( String address, Duration timeOfDate ) {
        return AllenBradleyHelper.WriteTimeOfDate( this, address, timeOfDate );
    }

    //endregion


    /**
     * 从PLC反馈的数据解析
     * @param response PLC的反馈数据
     * @param isRead 是否是返回的操作
     * @return 带有结果标识的最终数据
     */
    public static OperateResultExThree<byte[], Short, Boolean> ExtractActualData( byte[] response, boolean isRead ) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        int offset = 42;
        boolean hasMoreData = false;
        short dataType = 0;
        int count = Utilities.getUShort(response, offset);    // 剩余总字节长度，在剩余的字节里，有可能是一项数据，也有可能是多项
        if (Utilities.getInt(response, 46) == 0x8A) {
            // 多项数据
            offset = 50;
            int dataCount = Utilities.getUShort(response, offset);
            for (int i = 0; i < dataCount; i++) {
                int offsetStart = Utilities.getUShort(response, offset + 2 + i * 2) + offset;
                int offsetEnd = (i == dataCount - 1) ? response.length : (Utilities.getUShort(response, (offset + 4 + i * 2)) + offset);
                int err = Utilities.getUShort(response, offsetStart + 2);
                switch (err) {
                    case 0x04:
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley04());
                    case 0x05:
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley05());
                    case 0x06: {
                        // 06的错误码通常是数据长度太多了
                        // CC是符号返回，D2是符号片段返回， D5是列表数据
                        if (response[offset + 2] == (byte) 0xD2 || response[offset + 2] == (byte) 0xCC)
                            return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley06());
                        break;
                    }
                    case 0x0A:
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley0A());
                    case 0x13:
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley13());
                    case 0x1C:
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1C());
                    case 0x1E:
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1E());
                    case 0x26:
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley26());
                    case 0x00:
                        break;
                    default:
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.UnknownError());
                }

                if (isRead) {
                    for (int j = offsetStart + 6; j < offsetEnd; j++) {
                        data.write(response[j]);
                    }
                }
            }
        } else {
            byte err = response[offset + 6];
            switch (err) {
                case 0x04:
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley04());
                case 0x05:
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley05());
                case 0x06:
                    hasMoreData = true;
                    break;
                case 0x0A:
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley0A());
                case 0x13:
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley13());
                case 0x1C:
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1C());
                case 0x1E:
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1E());
                case 0x26:
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley26());
                case 0x00:
                    break;
                default:
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.UnknownError());
            }

            if (response[offset + 4] == (byte) 0xCD || response[offset + 4] == (byte) 0xD3)
                return OperateResultExThree.CreateSuccessResult(data.toByteArray(), dataType, hasMoreData);

            if (response[offset + 4] == (byte) 0xCC || response[offset + 4] == (byte) 0xD2) {
                for (int i = offset + 10; i < offset + 2 + count; i++) {
                    data.write(response[i]);
                }
                dataType = (short) Utilities.getUShort(response, offset + 8);
            } else if (response[offset + 4] == (byte) 0xD5) {
                for (int i = offset + 8; i < offset + 2 + count; i++) {
                    data.write(response[i]);
                }
            }
        }

        return OperateResultExThree.CreateSuccessResult(data.toByteArray(), dataType, hasMoreData);
    }
}
