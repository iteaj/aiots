package com.iteaj.plugin.protocol.plc.ab.profinet;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * AB PLC的辅助类，用来辅助生成基本的指令信息
 */
public class AllenBradleyHelper {

    //region Static Service Code

    /**
     * CIP命令中PCCC命令相关的信息
     */
    public static final int CIP_Execute_PCCC = 0x4B;
    /**
     * CIP命令中的读取数据的服务
     */
    public static final int CIP_READ_DATA = 0x4C;

    /**
     * CIP命令中的写数据的服务
     */
    public static final int CIP_WRITE_DATA = 0x4D;

    /**
     * CIP命令中的读并写的数据服务
     */
    public static final int CIP_READ_WRITE_DATA = 0x4E;

    /**
     * CIP命令中的读片段的数据服务
     */
    public static final int CIP_READ_FRAGMENT = 0x52;

    /**
     * CIP命令中的写片段的数据服务
     */
    public static final int CIP_WRITE_FRAGMENT = 0x53;

    /**
     * CIP指令中读取数据的列表
     */
    public static final int CIP_READ_LIST = 0x55;

    /**
     * CIP命令中的对数据读取服务
     */
    public static final int CIP_MULTIREAD_DATA = 0x1000;

    //endregion

    //region DataType Code

    /**
     * 日期的格式
     */
    public static final int CIP_Type_DATE = 0x08;

    /**
     * 时间的格式
     */
    public static final int CIP_Type_TIME = 0x09;

    /**
     * 日期时间格式，最完整的时间格式
     */
    public static final int CIP_Type_TimeAndDate = 0x0A;

    /**
     * 一天中的时间格式
     */
    public static final int CIP_Type_TimeOfDate = 0x0B;

    /**
     * bool型数据，一个字节长度
     */
    public static final int CIP_Type_Bool = 0xC1;

    /**
     * byte型数据，一个字节长度
     */
    public static final int CIP_Type_Byte = 0xC2;

    /**
     * 整型，两个字节长度
     */
    public static final int CIP_Type_Word = 0xC3;

    /**
     * 长整型，四个字节长度
     */
    public static final int CIP_Type_DWord = 0xC4;

    /**
     * 特长整型，8个字节
     */
    public static final int CIP_Type_LInt = 0xC5;

    /**
     * Unsigned 8-bit integer, USINT
     */
    public static final int CIP_Type_USInt = 0xC6;

    /**
     * Unsigned 16-bit integer, UINT
     */
    public static final int CIP_Type_UInt = 0xC7;

    /**
     * Unsigned 32-bit integer, UDINT
     */
    public static final int CIP_Type_UDint = 0xC8;

    /**
     * Unsigned 64-bit integer, ULINT
     */
    public static final int CIP_Type_ULint = 0xC9;

    /**
     * 实数数据，四个字节长度
     */
    public static final int CIP_Type_Real = 0xCA;

    /**
     * 实数数据，八个字节的长度
     */
    public static final int CIP_Type_Double = 0xCB;

    /**
     * 结构体数据，不定长度
     */
    public static final int CIP_Type_Struct = 0xCC;

    /**
     * 字符串数据内容
     */
    public static final int CIP_Type_String = 0xD0;

    /**
     * Bit string, 16-bits, WORD
     */
    public static final int CIP_Type_D1 = 0xD1;

    /**
     * Bit string, 32 bits, DWORD
     */
    public static final int CIP_Type_D2 = 0xD2;

    /**
     * 二进制数据内容
     */
    public static final int CIP_Type_BitArray = 0xD3;

    //endregion

    /**
     * 连接方的厂商标识
     */
    public static final short OriginatorVendorID = 0x1009;

    /**
     * 连接方的序列号
     */
    public static final int OriginatorSerialNumber = 0xC1A5460B;

    private static byte[] BuildRequestPathCommand( String address){
        return BuildRequestPathCommand(address, false);
    }

    /**
     * 创建包含路径的报文
     * @param address 地址信息
     * @return 报文信息
     */
    private static byte[] BuildRequestPathCommand( String address, boolean isConnectedAddress) {
        MemoryStream ms = new MemoryStream();
        int classid = -1;
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "class");
        if (extra.IsSuccess) {
            classid = extra.Content1;
            address = extra.Content2;
        }

        if (classid != -1) {
            int instanceid = address.startsWith("0x") ? Convert.ToInt32(address.substring(2), 16) : Convert.ToInt32(address);
            if (classid < 256) {
                ms.WriteByte(0x20);
                ms.WriteByte((byte) classid);
            } else {
                ms.WriteByte(0x21);
                ms.WriteByte(0x00);
                ms.WriteByte(BitConverter.GetBytes(classid)[0]);
                ms.WriteByte(BitConverter.GetBytes(classid)[1]);
            }

            if (instanceid < 256) {
                ms.WriteByte(0x24);
                ms.WriteByte((byte) instanceid);
            } else {
                ms.WriteByte(0x25);
                ms.WriteByte(0x00);
                ms.WriteByte(BitConverter.GetBytes(instanceid)[0]);
                ms.WriteByte(BitConverter.GetBytes(instanceid)[1]);
            }
        } else {

            String[] tagNames = address.split("\\.");

            for (int i = 0; i < tagNames.length; i++) {
                String strIndex = "";
                int indexFirst = tagNames[i].indexOf('[');
                int indexSecond = tagNames[i].indexOf(']');
                if (indexFirst > 0 && indexSecond > 0 && indexSecond > indexFirst) {
                    strIndex = tagNames[i].substring(indexFirst + 1, indexSecond);
                    tagNames[i] = tagNames[i].substring(0, indexFirst);
                }

                ms.WriteByte(0x91);                        // 固定
                byte[] nameBytes = tagNames[i].getBytes(StandardCharsets.UTF_8);
                ms.WriteByte((byte) nameBytes.length);    // 节点的长度值
                ms.Write(nameBytes, 0, nameBytes.length);
                if (nameBytes.length % 2 == 1) ms.WriteByte(0x00);

                if (!Utilities.IsStringNullOrEmpty(strIndex)) {
                    String[] indexes = strIndex.split(",");
                    for (int j = 0; j < indexes.length; j++) {
                        int index = Integer.parseInt(indexes[j]);
                        if (index < 256 && !isConnectedAddress) {
                            ms.WriteByte(0x28);
                            ms.WriteByte((byte) index);
                        } else if (index < 65536) {
                            ms.WriteByte(0x29);
                            ms.WriteByte(0x00);
                            ms.WriteByte(BitConverter.GetBytes(index)[0]);
                            ms.WriteByte(BitConverter.GetBytes(index)[1]);
                        } else {
                            ms.WriteByte(0x2A);
                            ms.WriteByte(0x00);
                            ms.Write(BitConverter.GetBytes(index));
                        }
                    }
                }
            }
        }
        return ms.ToArray();
    }

    /**
     * 打包生成一个请求读取数据的节点信息，CIP指令信息
     * @param address 地址
     * @param length 指代数组的长度
     * @return CIP的指令信息
     */
    public static byte[] PackRequsetRead( String address, int length ){
        return PackRequsetRead(address,length,false);
    }

    /**
     * 打包生成一个请求读取数据的节点信息，CIP指令信息
     * @param address 地址
     * @param length 指代数组的长度
     * @param isConnectedAddress 是否基于连接的协议的格式
     * @return CIP的指令信息
     */
    public static byte[] PackRequsetRead( String address, int length, boolean isConnectedAddress ) {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = CIP_READ_DATA;
        offset++;

        byte[] requestPath = BuildRequestPathCommand( address, isConnectedAddress );
        System.arraycopy(requestPath, 0, buffer, offset, requestPath.length);
        offset += requestPath.length;

        buffer[1] = (byte)((offset - 2) / 2);
        buffer[offset++] = Utilities.getBytes( length )[0];
        buffer[offset++] = Utilities.getBytes( length )[1];

        byte[] data = new byte[offset];
        System.arraycopy( buffer, 0, data, 0, offset );
        return data;
    }

    /**
     * 打包生成一个请求读取数据片段的节点信息，CIP指令信息
     * @param address 节点的名称
     * @param startIndex 起始的索引位置
     * @param length 读取的数据长度，对于short来说，最大是489长度
     * @return CIP的指令信息
     */
    public static byte[] PackRequestReadSegment(String address, int startIndex, int length ) throws IOException {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = CIP_READ_FRAGMENT;
        offset++;


        byte[] requestPath = BuildRequestPathCommand( address );
        System.arraycopy(requestPath, 0, buffer, offset, requestPath.length);
        offset += requestPath.length;

        buffer[1] = (byte)((offset - 2) / 2);
        buffer[offset++] = Utilities.getBytes( length )[0];
        buffer[offset++] = Utilities.getBytes( length )[1];
        buffer[offset++] = Utilities.getBytes( startIndex )[0];
        buffer[offset++] = Utilities.getBytes( startIndex )[1];
        buffer[offset++] = Utilities.getBytes( startIndex )[2];
        buffer[offset++] = Utilities.getBytes( startIndex )[3];

        byte[] data = new byte[offset];
        System.arraycopy( buffer, 0, data, 0, offset );
        return data;
    }

    /**
     * 根据指定的数据和类型，生成对应的数据
     * @param address 地址信息
     * @param typeCode 数据类型
     * @param value 字节值
     * @return CIP的指令信息
     */
    public static byte[] PackRequestWrite( String address, int typeCode, byte[] value ) {
        return PackRequestWrite(address,typeCode,value,1);
    }

    /**
     * 根据指定的数据和类型，生成对应的数据
     * @param address 地址信息
     * @param typeCode 数据类型
     * @param value 字节值
     * @param length 如果节点为数组，就是数组长度
     * @return CIP的指令信息
     */
    public static byte[] PackRequestWrite( String address, int typeCode, byte[] value, int length ){
        return PackRequestWrite(address,typeCode,value,length,false);
    }

    /**
     * 根据指定的数据和类型，生成对应的数据
     * @param address 地址信息
     * @param typeCode 数据类型
     * @param value 字节值
     * @param length 如果节点为数组，就是数组长度
     * @param isConnectedAddress 是否基于连接的协议实现
     * @return CIP的指令信息
     */
    public static byte[] PackRequestWrite( String address, int typeCode, byte[] value, int length, boolean isConnectedAddress ) {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = CIP_WRITE_DATA;
        offset++;

        byte[] requestPath = BuildRequestPathCommand( address, isConnectedAddress );
        System.arraycopy(requestPath, 0, buffer, offset, requestPath.length);
        offset += requestPath.length;

        buffer[1] = (byte)((offset - 2) / 2);

        buffer[offset++] = Utilities.getBytes( typeCode )[0];     // 数据类型
        buffer[offset++] = Utilities.getBytes( typeCode )[1];

        buffer[offset++] = Utilities.getBytes( length )[0];       // 固定
        buffer[offset++] = Utilities.getBytes( length )[1];

        if (value == null) value = new byte[0];
        byte[] data = new byte[value.length + offset];
        System.arraycopy( buffer, 0, data, 0, offset );
        Utilities.ByteArrayCopyTo( value, data, offset );                              // 数值
        return data;
    }

    public static byte[] PackRequestWriteSegment( String address, int typeCode, byte[] value, int startIndex, int length, boolean isConnectedAddress ) {
        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = CIP_WRITE_FRAGMENT;
        offset++;

        byte[] requestPath = BuildRequestPathCommand(address, isConnectedAddress);
        Utilities.ByteArrayCopyTo(requestPath,buffer, offset);
        offset += requestPath.length;

        buffer[1] = (byte) ((offset - 2) / 2);

        buffer[offset++] = BitConverter.GetBytes(typeCode)[0];     // 数据类型
        buffer[offset++] = BitConverter.GetBytes(typeCode)[1];

        buffer[offset++] = BitConverter.GetBytes(length)[0];       // 固定
        buffer[offset++] = BitConverter.GetBytes(length)[1];

        buffer[offset++] = BitConverter.GetBytes(startIndex)[0];
        buffer[offset++] = BitConverter.GetBytes(startIndex)[1];
        buffer[offset++] = BitConverter.GetBytes(startIndex)[2];
        buffer[offset++] = BitConverter.GetBytes(startIndex)[3];

        if (value == null) value = new byte[0];
        byte[] data = new byte[value.length + offset];
        Array.Copy(buffer, 0, data, 0, offset);
        Utilities.ByteArrayCopyTo(value, data, offset);                              // 数值
        return data;
    }

    /**
     * 写入Bool数据的基本指令信息
     * @param address 地址
     * @param value 值
     * @return 报文信息
     * @throws IOException 错误信息
     */
    public static byte[] PackRequestWrite( String address, boolean value ) throws IOException {
        OperateResultExTwo<String,Integer> analysis = AnalysisArrayIndex( address );
        address = analysis.Content1;
        int bitIndex = analysis.Content2;

        address = address + "[" + (bitIndex / 32) + "]";
        int valueOr = 0;
        int valueAnd = -1;
        if (value) valueOr = 1 << bitIndex;
        else valueAnd = ~(1 << bitIndex);

        byte[] buffer = new byte[1024];
        int offset = 0;
        buffer[offset++] = CIP_READ_WRITE_DATA;
        offset++;

        byte[] requestPath = BuildRequestPathCommand( address );
        System.arraycopy(requestPath,0,buffer,offset,requestPath.length);
        offset += requestPath.length;

        buffer[1] = (byte)((offset - 2) / 2);

        buffer[offset++] = 0x04;                                     // 掩盖长度
        buffer[offset++] = 0x00;

        System.arraycopy(Utilities.getBytes(valueOr),0,buffer,offset,4);
        offset += 4;
        System.arraycopy(Utilities.getBytes(valueAnd),0,buffer,offset,4);
        offset += 4;

        byte[] data = new byte[offset];
        System.arraycopy(buffer, 0, data,0, offset);
        return data;
    }

    /**
     * 分析地址数据信息里的位索引的信息，例如a[10]  返回 a 和 10 索引，如果没有指定索引，就默认为0
     * @param address 数据地址
     * @return 地址信息及位索引
     */
    public static OperateResultExTwo<String,Integer> AnalysisArrayIndex(String address ) {
        int arrayIndex = 0;
        if (!address.endsWith("]")) return OperateResultExTwo.CreateSuccessResult(address, arrayIndex);

        int index = address.lastIndexOf('[');
        if (index < 0) return OperateResultExTwo.CreateSuccessResult(address, arrayIndex);

        address = address.substring(0, address.length() - 1);
        try {
            arrayIndex = Integer.parseInt(address.substring(index + 1));
            address = address.substring(0, index);
            return OperateResultExTwo.CreateSuccessResult(address, arrayIndex);
        } catch (Exception ex) {
            return OperateResultExTwo.CreateSuccessResult(address, arrayIndex);
        }
    }


    /**
     * 将所有的cip指定进行打包操作。
     * @param portSlot PLC所在的面板槽号
     * @param cips 所有的cip打包指令信息
     * @return 包含服务的信息
     * @throws IOException
     */
    public static byte[] PackCommandService( byte[] portSlot, byte[]... cips ) throws IOException {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        // type id   0xB2:UnConnected Data Item  0xB1:Connected Data Item  0xA1:Connect Address Item
        ms.write( 0xB2 );
        ms.write( 0x00 );
        ms.write( 0x00 );     // 后续数据的长度
        ms.write( 0x00 );

        ms.write( 0x52 );     // 服务
        ms.write( 0x02 );     // 请求路径大小
        ms.write( 0x20 );     // 请求路径
        ms.write( 0x06 );
        ms.write( 0x24 );
        ms.write( 0x01 );
        ms.write( 0x0A );     // 超时时间
        ms.write( 0xF0 );
        ms.write( 0x00 );     // CIP指令长度
        ms.write( 0x00 );

        int count = 0;
        if (cips.length == 1)
        {
            ms.write( cips[0], 0, cips[0].length );
            count += cips[0].length;
            if (cips[0].length % 2 == 1) ms.write( 0x00 );
        }
        else
        {
            ms.write( 0x0A );   // 固定
            ms.write( 0x02 );
            ms.write( 0x20 );
            ms.write( 0x02 );
            ms.write( 0x24 );
            ms.write( 0x01 );
            count += 8;

            ms.write( Utilities.getBytes( (short) cips.length ), 0, 2 );  // 写入项数
            short offect = (short) (0x02 + 2 * cips.length);
            count += 2 * cips.length;

            for (int i = 0; i < cips.length; i++)
            {
                ms.write( Utilities.getBytes( offect ), 0, 2 );
                offect = (short) (offect + cips[i].length);
            }

            for (int i = 0; i < cips.length; i++)
            {
                ms.write( cips[i], 0, cips[i].length );
                count += cips[i].length;
            }
        }

        ms.write( (byte)((portSlot.length + 1) / 2) );     // Path Size
        ms.write( 0x00 );
        ms.write( portSlot, 0, portSlot.length );
        if (portSlot.length % 2 == 1) ms.write( 0x00 );

        byte[] data = ms.toByteArray( );
        ms.close( );
        data[12] = Utilities.getBytes(count)[0];
        data[13] = Utilities.getBytes(count)[1];
        data[2] = Utilities.getBytes((short)(data.length - 4))[0];
        data[3] = Utilities.getBytes((short)(data.length - 4))[1];
        return data;
    }

    /**
     * 将所有的cip指定进行打包操作。
     * @param command 指令信息
     * @param code 服务器的代号信息
     * @param isConnected 是否基于连接的服务
     * @return 包含服务的信息
     */
    public static byte[] PackCommandSingleService( byte[] command, int code, boolean isConnected )
    {
        if (command == null) command = new byte[0];

        byte[] buffer = isConnected ? new byte[6 + command.length] : new byte[4 + command.length];
        buffer[0] = BitConverter.GetBytes( code )[0];
        buffer[1] = BitConverter.GetBytes( code )[1];
        buffer[2] = BitConverter.GetBytes( buffer.length - 4 )[0];
        buffer[3] = BitConverter.GetBytes( buffer.length - 4 )[1];

        Utilities.ByteArrayCopyTo(command, buffer, isConnected ? 6 : 4 );
        return buffer;
    }


    /**
     * 生成读取直接节点数据信息的内容
     * @param service cip指令内容
     * @return 最终的指令值
     * @throws IOException
     */
    public static byte[] PackCommandSpecificData( byte[]... service ) {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        ms.write(0x00);
        ms.write(0x00);
        ms.write(0x00);
        ms.write(0x00);
        ms.write(0x01);     // 超时
        ms.write(0x00);
        ms.write(Utilities.getBytes(service.length)[0]);    // 项数
        ms.write(Utilities.getBytes(service.length)[1]);
        for (int i = 0; i < service.length; i++) {
            ms.write(service[i], 0, service[i].length);
        }
        return ms.toByteArray();
    }

    private static byte[] PackExecutePCCC( byte[] pccc )
    {
        MemoryStream ms = new MemoryStream( );
        ms.WriteByte( CIP_Execute_PCCC );
        ms.WriteByte( 0x02 );
        ms.WriteByte( 0x20 );  //
        ms.WriteByte( 0x67 );
        ms.WriteByte( 0x24 );  // Requestor ID
        ms.WriteByte( 0x01 );
        ms.WriteByte( 0x07 );  // CIP PCCC Object
        ms.WriteByte( 0x09 );
        ms.WriteByte( 0x10 );
        ms.WriteByte( 0x0B );  // CIP Serial Number
        ms.WriteByte( 0x46 );
        ms.WriteByte( 0xA5 );
        ms.WriteByte( 0xC1 );
        ms.Write( pccc );

        byte[] buffer = ms.ToArray( );
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes( AllenBradleyHelper.OriginatorVendorID ), buffer, 7 );
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes( AllenBradleyHelper.OriginatorSerialNumber ), buffer, 9 );
        return buffer;
    }

    /**
     * 打包一个PCCC的读取的命令报文
     * @param tns 请求序号信息
     * @param address 请求的文件地址，地址示例：N7:1
     * @param length 请求的字节长度
     * @return PCCC的读取报文信息
     */
    public static OperateResultExOne<byte[]> PackExecutePCCCRead(int tns, String address, short length )
    {
        OperateResultExOne<byte[]> command = AllenBradleyDF1Serial.BuildProtectedTypedLogicalReadWithThreeAddressFields( tns, address, length );
        if (!command.IsSuccess) return command;

        return OperateResultExOne.CreateSuccessResult( PackExecutePCCC( command.Content ) );
    }

    /**
     * 打包一个PCCC的写入的命令报文
     * @param tns 请求序号信息
     * @param address 请求的文件地址，地址示例：N7:1
     * @param value 写入的原始数据信息
     * @return PCCC的写入报文信息
     */
    public static OperateResultExOne<byte[]> PackExecutePCCCWrite( int tns, String address, byte[] value )
    {
        OperateResultExOne<byte[]> command = AllenBradleyDF1Serial.BuildProtectedTypedLogicalWriteWithThreeAddressFields( tns, address, value );
        if (!command.IsSuccess) return command;

        return OperateResultExOne.CreateSuccessResult( PackExecutePCCC( command.Content ) );
    }

    public static OperateResultExOne<byte[]> PackExecutePCCCWrite( int tns, String address, int bitIndex, boolean value )
    {
        OperateResultExOne<byte[]> command = AllenBradleyDF1Serial.BuildProtectedTypedLogicalMaskWithThreeAddressFields( tns, address, bitIndex, value );
        if (!command.IsSuccess) return command;

        return OperateResultExOne.CreateSuccessResult( PackExecutePCCC( command.Content ) );
    }

    /**
     * 向PLC注册会话ID的报文<br />
     * Register a message with the PLC for the session ID
     * @return 报文信息 -> Message information
     */
    public static byte[] RegisterSessionHandle( )
    {
        return RegisterSessionHandle(null);
    }

    /**
     * 向PLC注册会话ID的报文<br />
     * Register a message with the PLC for the session ID
     * @param senderContext 发送的上下文信息
     * @return 报文信息 -> Message information
     */
    public static byte[] RegisterSessionHandle( byte[] senderContext )
    {
        byte[] commandSpecificData = new byte[] { 0x01, 0x00, 0x00, 0x00, };
        return AllenBradleyHelper.PackRequestHeader( 0x65, 0, commandSpecificData, senderContext );
    }

    /**
     * 获取卸载一个已注册的会话的报文<br />
     * Get a message to uninstall a registered session
     * @param sessionHandle 当前会话的ID信息
     * @return 字节报文信息 -> BYTE message information
     */
    public static byte[] UnRegisterSessionHandle( int sessionHandle ) {
        return AllenBradleyHelper.PackRequestHeader(0x66, sessionHandle, new byte[0]);
    }

    /**
     * 初步检查返回的CIP协议的报文是否正确<br />
     * Initially check whether the returned CIP protocol message is correct
     * @param response CIP的报文信息
     * @return 是否正确的结果信息
     */
    public static OperateResult CheckResponse(byte[] response )
    {
        try
        {
            int status = Utilities.getInt( response, 8 );
            if (status == 0) return OperateResult.CreateSuccessResult( );

            String msg = "";
            switch (status)
            {
                case 0x01: msg = StringResources.Language.AllenBradleySessionStatus01(); break;
                case 0x02: msg = StringResources.Language.AllenBradleySessionStatus02(); break;
                case 0x03: msg = StringResources.Language.AllenBradleySessionStatus03(); break;
                case 0x64: msg = StringResources.Language.AllenBradleySessionStatus64(); break;
                case 0x65: msg = StringResources.Language.AllenBradleySessionStatus65(); break;
                case 0x69: msg = StringResources.Language.AllenBradleySessionStatus69(); break;
                default:   msg = StringResources.Language.UnknownError(); break;
            }

            return new OperateResult( status, msg );
        }
        catch (Exception ex)
        {
            return new OperateResult( ex.getMessage() );
        }
    }


    /**
     * 将CommandSpecificData的命令，打包成可发送的数据指令
     * @param command 实际的命令暗号
     * @param session 当前会话的id
     * @param commandSpecificData CommandSpecificData命令
     * @return 最终可发送的数据命令
     */
    public static byte[] PackRequestHeader( int command, int session, byte[] commandSpecificData )
    {
        return PackRequestHeader(command, session, commandSpecificData, null);
    }

    public static byte[] PackRequestHeader( int command, int session, byte[] commandSpecificData, byte[] senderContext )
    {
        if ( commandSpecificData == null ) commandSpecificData = new byte[0];
        byte[] buffer = new byte[commandSpecificData.length + 24];
        System.arraycopy( commandSpecificData, 0, buffer, 24, commandSpecificData.length );
        System.arraycopy( Utilities.getBytes(command), 0, buffer, 0, 2 );
        System.arraycopy( Utilities.getBytes(commandSpecificData.length ), 0, buffer, 2, 2 );

        if (senderContext != null) Utilities.ByteArrayCopyTo(senderContext, buffer, 12 );
        System.arraycopy( Utilities.getBytes(session), 0, buffer, 4, 4 );
        return buffer;
    }

    /**
     * 从PLC反馈的数据解析
     * @param response PLC的反馈数据
     * @param isRead 是否是返回的操作
     * @return 带有结果标识的最终数据
     */
    public static OperateResultExThree<byte[], Short, Boolean> ExtractActualData(byte[] response, boolean isRead )
    {
        ArrayList<Byte> data = new ArrayList<>( );

        int offset = 38;
        boolean hasMoreData = false;
        short dataType = 0;
        short count = Utilities.getShort( response, 38 );    // 剩余总字节长度，在剩余的字节里，有可能是一项数据，也有可能是多项
        if (Utilities.getInt( response, 40 ) == 0x8A)
        {
            // 多项数据
            offset = 44;
            int dataCount = Utilities.getShort( response, offset );
            for (int i = 0; i < dataCount; i++)
            {
                int offectStart = Utilities.getShort( response, offset + 2 + i * 2 ) + offset;
                int offectEnd = (i == dataCount - 1) ? response.length : (Utilities.getShort( response, (offset + 4 + i * 2) ) + offset);
                short err = Utilities.getShort( response, offectStart + 2 );
                switch (err)
                {
                    case 0x04: return new OperateResultExThree<>( err, StringResources.Language.AllenBradley04() );
                    case 0x05: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley05() );
                    case 0x06:
                    {
                        // 06的错误码通常是数据长度太多了
                        // CC是符号返回，D2是符号片段返回
                        if (response[offset + 2] == (byte)0xD2 || response[offset + 2] == (byte) 0xCC)
                            return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley06() );
                        break;
                    }
                    case 0x0A: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley0A() );
                    case 0x0C: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley0C() );
                    case 0x13: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley13() );
                    case 0x1C: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1C() );
                    case 0x1E: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1E() );
                    case 0x26: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley26() );
                    case 0x00: break;
                    default: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.UnknownError() );
                }

                if (isRead)
                {
                    for (int j = offectStart + 6; j < offectEnd; j++)
                    {
                        data.add( response[j] );
                    }
                }
            }
        }
        else
        {
            // 单项数据
            byte err = response[offset + 4];
            switch (err)
            {
                case 0x04: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley04() );
                case 0x05: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley05() );
                case 0x06:
                {
                    hasMoreData = true;
                    break;
                }
                case 0x0A: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley0A() );
                case 0x0C: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley0C() );
                case 0x13: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley13() );
                case 0x1C: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1C() );
                case 0x1E: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1E() );
                case 0x26: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley26() );
                case 0x00: break;
                default: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.UnknownError() );
            }

            if (response[offset + 2] == (byte)0xCD || response[offset + 2] == (byte)0xD3) return OperateResultExThree.CreateSuccessResult( new byte[0], dataType, hasMoreData );

            if (response[offset + 2] == (byte)0xCC || response[offset + 2] == (byte)0xD2)
            {
                for (int i = offset + 8; i < offset + 2 + count; i++)
                {
                    data.add( response[i] );
                }
                dataType = Utilities.getShort( response, offset + 6 );
            }
            else if (response[offset + 2] == (byte)0xD5)
            {
                for (int i = offset + 6; i < offset + 2 + count; i++)
                {
                    data.add( response[i] );
                }
            }
        }

        byte[] buffer = new byte[ data.size()];
        for (int i = 0; i< buffer.length; i++){
            buffer[i] = (byte) data.get(i);
        }
        return OperateResultExThree.CreateSuccessResult( buffer , dataType, hasMoreData);
    }

    /**
     * 从PLC里读取当前PLC的型号信息<br />
     * Read the current PLC model information from the PLC
     * @param plc PLC对象
     * @return 型号数据信息
     */
    public static OperateResultExOne<String> ReadPlcType( IReadWriteDevice plc ) {
        byte[] buffer = SoftBasic.HexStringToBytes("00 00 00 00 00 00 02 00 00 00 00 00 b2 00 06 00 01 02 20 01 24 01");
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(buffer);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        if (read.Content.length > 59 && (read.Content.length >= (59 +(read.Content[58] & 0xff))))
            return OperateResultExOne.CreateSuccessResult(new String(read.Content, 59, read.Content[58] & 0xff, StandardCharsets.UTF_8));
        return new OperateResultExOne<String>("Data is too short: " + SoftBasic.ByteToHexString(read.Content, ' '));
    }

    /**
     * 将所有的cip指定进行打包操作。
     * @param portSlot PLC所在的面板槽号
     * @param cips 所有的cip打包指令信息
     * @return 包含服务的信息
     */
    public static byte[] PackCleanCommandService( byte[] portSlot, byte[]... cips ) {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        // type id   0xB2:UnConnected Data Item  0xB1:Connected Data Item  0xA1:Connect Address Item
        ms.write(0xB2);
        ms.write(0x00);
        ms.write(0x00);     // 后续数据的长度
        ms.write(0x00);

        if (cips.length == 1) {
            ms.write(cips[0], 0, cips[0].length);
        } else {
            ms.write(0x0A);   // 固定
            ms.write(0x02);
            ms.write(0x20);
            ms.write(0x02);
            ms.write(0x24);
            ms.write(0x01);

            ms.write(Utilities.getBytes((short) cips.length), 0, 2);  // 写入项数
            short offect = (short) (0x02 + 2 * cips.length);

            for (int i = 0; i < cips.length; i++) {
                ms.write(Utilities.getBytes(offect), 0, 2);
                offect = (short) (offect + cips[i].length);
            }

            for (int i = 0; i < cips.length; i++) {
                ms.write(cips[i], 0, cips[i].length);
            }
        }

        ms.write((byte) ((portSlot.length + 1) / 2));     // Path Size
        ms.write(0x00);
        ms.write(portSlot, 0, portSlot.length);
        if (portSlot.length % 2 == 1) ms.write(0x00);

        byte[] data = ms.toByteArray();
        System.arraycopy(Utilities.getBytes((short) (data.length - 4)), 0, data, 2, 2);
        return data;
    }



    //region Date ReadWrite

    /**
     * 读取指定地址的日期数据，最小日期为 1970年1月1日，当PLC的变量类型为 "Date" 和 "TimeAndDate" 时，都可以用本方法读取。<br />
     * Read the date data of the specified address. The minimum date is January 1, 1970. When the PLC variable type is "Date" and "TimeAndDate", this method can be used to read.
     * @param plc 当前的通信对象信息
     * @param address PLC里变量的地址
     * @return 日期结果对象
     */
    public static OperateResultExOne<Date> ReadDate(IReadWriteCip plc, String address ) {
        OperateResultExOne<Long> read = plc.ReadInt64(address);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1970, Calendar.JANUARY, 1 );
        // 分两步增加, 第一步增加天，第二步增加一天的毫秒数
        calendar.add(Calendar.DATE, (int) (read.Content / 1000000000L / 86400L));
        calendar.add(Calendar.MILLISECOND, (int) ((read.Content / 1000000L) % 86400000L));

        return OperateResultExOne.CreateSuccessResult(calendar.getTime());
    }

    /**
     * 使用日期格式（Date）将指定的数据写入到指定的地址里，PLC的地址类型变量必须为 "Date"，否则写入失败。<br />
     * Use the date format (Date) to write the specified data to the specified address. The PLC address type variable must be "Date", otherwise the writing will fail.
     * @param plc 当前的通信对象信息
     * @param address PLC里变量的地址
     * @param date 时间信息
     * @return 是否写入成功
     */
    public static OperateResult WriteDate( IReadWriteCip plc, String address, Date date )
    {
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(1970, Calendar.JANUARY, 1);

        Calendar end = Calendar.getInstance();
        end.clear();
        end.set(date.getYear() + 1900, date.getMonth(), date.getDate());
        long tick = (end.getTimeInMillis() - start.getTimeInMillis()) * 1000_000L;
        return plc.WriteTag( address, (short) AllenBradleyHelper.CIP_Type_DATE, plc.getByteTransform().TransByte( tick ), 1 );
    }

    /**
     * 使用日期 + 时间格式（TimeAndDate）将指定的数据写入到指定的地址里，PLC的地址类型变量必须为 "TimeAndDate"，否则写入失败。<br />
     * Use the date + time format (TimeAndDate) to write the specified data to the specified address. The PLC address type variable must be "TimeAndDate", otherwise the writing will fail.
     * @param plc 当前的通信对象信息
     * @param address PLC里变量的地址
     * @param date 时间信息
     * @return 是否写入成功
     */
    public static OperateResult WriteTimeAndDate( IReadWriteCip plc, String address, Date date )
    {
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(1970, Calendar.JANUARY, 1);

        long tick = (date.getTime() - start.getTimeInMillis()) * 1000_000;
        return plc.WriteTag( address, (short)AllenBradleyHelper.CIP_Type_TimeAndDate, plc.getByteTransform().TransByte( tick ), 1 );
    }

    /**
     * 读取指定地址的时间数据，最小时间为 0，如果获取秒，可以访问 {@link Duration#getSeconds()}，当PLC的变量类型为 "Time" 和 "TimeOfDate" 时，都可以用本方法读取。<br />
     * Read the time data of the specified address. The minimum time is 0. If you get seconds, you can access {@link Duration#getSeconds()}.
     * When the PLC variable type is "Time" and "TimeOfDate", you can use this Method to read.
     * @param plc 当前的通信对象信息
     * @param address PLC里变量的地址
     * @return 时间的结果对象
     */
    public static OperateResultExOne<Duration> ReadTime( IReadWriteCip plc, String address )
    {
        OperateResultExOne<Long> read = plc.ReadInt64( address );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        long tick = read.Content / 1000_000;
        return OperateResultExOne.CreateSuccessResult( Duration.ofMillis( tick ) );
    }

    /**
     * 使用时间格式（TIME）将时间数据写入到PLC中指定的地址里去，PLC的地址类型变量必须为 "TIME"，否则写入失败。<br />
     * Use the time format (TIME) to write the time data to the address specified in the PLC. The PLC address type variable must be "TIME", otherwise the writing will fail.
     * @param plc 当前的通信对象信息
     * @param address PLC里变量的地址
     * @param time 时间参数变量
     * @return 是否写入成功
     */
    public static OperateResult WriteTime( IReadWriteCip plc, String address, Duration time )
    {
        return plc.WriteTag( address, (short) AllenBradleyHelper.CIP_Type_TIME, plc.getByteTransform().TransByte( time.toMillis() * 1000_000 ), 1 );
    }

    /**
     * 使用时间格式（TimeOfDate）将时间数据写入到PLC中指定的地址里去，PLC的地址类型变量必须为 "TimeOfDate"，否则写入失败。<br />
     * Use the time format (TimeOfDate) to write the time data to the address specified in the PLC. The PLC address type variable must be "TimeOfDate", otherwise the writing will fail.
     * @param plc 当前的通信对象信息
     * @param address PLC里变量的地 PLC里变量的地址
     * @param timeOfDate 时间参数变量
     * @return 是否写入成功
     */
    public static OperateResult WriteTimeOfDate( IReadWriteCip plc, String address, Duration timeOfDate )
    {
        return plc.WriteTag( address, (short)AllenBradleyHelper.CIP_Type_TimeOfDate, plc.getByteTransform().TransByte( timeOfDate.toMillis() * 1000_000 ), 1 );
    }

    //endregion
}
