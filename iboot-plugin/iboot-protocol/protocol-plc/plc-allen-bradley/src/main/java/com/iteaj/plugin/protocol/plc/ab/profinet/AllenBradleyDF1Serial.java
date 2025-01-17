package com.iteaj.plugin.protocol.plc.ab.profinet;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.BitConverter;
import com.iteaj.plugin.protocol.plc.core.types.MemoryStream;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;


public class AllenBradleyDF1Serial {

    private static void AddLengthToMemoryStream(MemoryStream ms, short value )
    {
        if (value < 255)
            ms.WriteByte( (byte)value );                     // File Number
        else
        {
            ms.WriteByte( 0xFF );
            ms.WriteByte( BitConverter.GetBytes( value )[0] );
            ms.WriteByte( BitConverter.GetBytes( value )[1] );
        }
    }

    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalReadWithThreeAddressFields(int tns, String address, short length )
    {
        OperateResultExOne<AllenBradleySLCAddress> analysis = AllenBradleySLCAddress.ParseFrom( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        // AB PLC DF1手册 Page 104
        MemoryStream ms = new MemoryStream( );
        ms.WriteByte( 0x0F );                                            // Command
        ms.WriteByte( 0x00 );                                            // STS
        ms.WriteByte( BitConverter.GetBytes( tns )[0] );
        ms.WriteByte( BitConverter.GetBytes( tns )[1] );
        ms.WriteByte( 0xA2 );                                            // Function
        ms.WriteByte( BitConverter.GetBytes( length )[0] );              // Bytes Length
        AddLengthToMemoryStream( ms, analysis.Content.DbBlock );         // file number
        ms.WriteByte( analysis.Content.DataCode );                       // File Type
        AddLengthToMemoryStream( ms, (short) analysis.Content.getAddressStart() );    // element number
        AddLengthToMemoryStream( ms, (short) 0x00 );                             // sub-element number

        return OperateResultExOne.CreateSuccessResult( ms.ToArray( ) );
    }

    /**
     * 构建0F-A2命令码的报文读取指令，用来读取文件数据。适用 Micro-Logix1000,SLC500,SLC 5/03,SLC 5/04, PLC-5，地址示例：N7:1<br />
     * Construct a message read instruction of 0F-A2 command code to read file data. Applicable to Micro-Logix1000, SLC500, SLC 5/03, SLC 5/04, PLC-5, address example: N7:1<br /><br />
     *
     * 对于SLC 5/01或SLC 5/02而言，一次最多读取82个字节。对于 03 或是 04 为225，236字节取决于是否应用DF1驱动
     * @param dstNode 目标节点号
     * @param srcNode 原节点号
     * @param tns 消息号
     * @param address PLC的地址信息
     * @param length 读取的数据长度
     * @return 初步的报文信息
     */
    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalReadWithThreeAddressFields( byte dstNode, byte srcNode, int tns, String address, short length )
    {
        OperateResultExOne<byte[]> build = BuildProtectedTypedLogicalReadWithThreeAddressFields( tns, address, length );
        if (!build.IsSuccess) return OperateResultExOne.CreateFailedResult( build );

        return OperateResultExOne.CreateSuccessResult( SoftBasic.SpliceArray( new byte[] { dstNode, srcNode }, build.Content ) );
    }

    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalWriteWithThreeAddressFields( int tns, String address, byte[] data )
    {
        OperateResultExOne<AllenBradleySLCAddress> analysis = AllenBradleySLCAddress.ParseFrom( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        // AB PLC DF1手册 Page 104
        MemoryStream ms = new MemoryStream( );
        ms.WriteByte( 0x0F );                                            // Command
        ms.WriteByte( 0x00 );                                            // STS
        ms.WriteByte( BitConverter.GetBytes( tns )[0] );
        ms.WriteByte( BitConverter.GetBytes( tns )[1] );
        ms.WriteByte( 0xAA );                                            // Function
        ms.WriteByte( BitConverter.GetBytes( data.length )[0] );         // Bytes Length
        AddLengthToMemoryStream( ms, analysis.Content.DbBlock );         // file number
        ms.WriteByte( analysis.Content.DataCode );                       // File Type
        AddLengthToMemoryStream( ms, (short) analysis.Content.getAddressStart() );    // element number
        AddLengthToMemoryStream( ms, (short) 0x00 );                             // sub-element number

        ms.Write( data );
        return OperateResultExOne.CreateSuccessResult( ms.ToArray( ) );
    }

    /**
     * 构建0F-AA命令码的写入读取指令，用来写入文件数据。适用 Micro-Logix1000,SLC500,SLC 5/03,SLC 5/04, PLC-5，地址示例：N7:1<br />
     * Construct a write and read command of 0F-AA command code to write file data. Applicable to Micro-Logix1000, SLC500, SLC 5/03, SLC 5/04, PLC-5, address example: N7:1<br /><br />
     *
     * 对于SLC 5/01或SLC 5/02而言，一次最多读取82个字节。对于 03 或是 04 为225，236字节取决于是否应用DF1驱动
     * @param dstNode 目标节点号
     * @param srcNode 原节点号
     * @param tns 消息号
     * @param address PLC的地址信息
     * @param data 写入的数据内容
     * @return 初步的报文信息
     */
    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalWriteWithThreeAddressFields( byte dstNode, byte srcNode, int tns, String address, byte[] data )
    {
        OperateResultExOne<byte[]> build = BuildProtectedTypedLogicalWriteWithThreeAddressFields( tns, address, data );
        if (!build.IsSuccess) return OperateResultExOne.CreateFailedResult( build );

        return OperateResultExOne.CreateSuccessResult( SoftBasic.SpliceArray( new byte[] { dstNode, srcNode }, build.Content ) );
    }

    /**
     * 构建0F-AB的掩码写入的功能
     * @param tns 消息号
     * @param address PLC的地址信息
     * @param bitIndex 位索引信息
     * @param value 通断值
     * @return 命令报文
     */
    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalMaskWithThreeAddressFields( int tns, String address, int bitIndex, boolean value )
    {
        int mask = 1 << bitIndex;
        OperateResultExOne<AllenBradleySLCAddress> analysis = AllenBradleySLCAddress.ParseFrom( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        MemoryStream ms = new MemoryStream( );
        ms.WriteByte( 0x0F );                                            // Command
        ms.WriteByte( 0x00 );                                            // STS
        ms.WriteByte( BitConverter.GetBytes( tns )[0] );
        ms.WriteByte( BitConverter.GetBytes( tns )[1] );
        ms.WriteByte( 0xAB );                                            // Function
        ms.WriteByte( 0x02 );                                            // Bytes Length
        AddLengthToMemoryStream( ms, analysis.Content.DbBlock );         // file number
        ms.WriteByte( analysis.Content.DataCode );                       // File Type
        AddLengthToMemoryStream( ms, (short)analysis.Content.getAddressStart() );    // element number
        AddLengthToMemoryStream( ms, (short) 0x00 );                                 // sub-element number

        ms.WriteByte( BitConverter.GetBytes( mask )[0] );
        ms.WriteByte( BitConverter.GetBytes( mask )[1] );
        if (value)
        {
            ms.WriteByte( BitConverter.GetBytes( mask )[0] );
            ms.WriteByte( BitConverter.GetBytes( mask )[1] );
        }
        else
        {
            ms.WriteByte( 0x00 );
            ms.WriteByte( 0x00 );
        }
        return OperateResultExOne.CreateSuccessResult( ms.ToArray( ) );
    }

    /**
     * 根据错误代码，来获取错误的具体描述文本
     * @param code 错误的代码，非0
     * @return 错误的描述文本信息
     */
    public static String GetExtStatusDescription( byte code )
    {
        switch (code)
        {
            case 0x01: return "A field has an illegal value";
            case 0x02: return "Less levels specified in address than minimum for any address";
            case 0x03: return "More levels specified in address than system supports";
            case 0x04: return "Symbol not found";
            case 0x05: return "Symbol is of improper format";
            case 0x06: return "Address doesn’t point to something usable";
            case 0x07: return "File is wrong size";
            case 0x08: return "Cannot complete request, situation has changed since the start of the command";
            case 0x09: return "Data or file is too large";
            case 0x0A: return "Transaction size plus word address is too large";
            case 0x0B: return "Access denied, improper privilege";
            case 0x0C: return "Condition cannot be generated  resource is not available";
            case 0x0D: return "Condition already exists  resource is already available";
            case 0x0E: return "Command cannot be executed";
            case 0x0F: return "Histogram overflow";
            case 0x10: return "No access";
            case 0x11: return "Illegal data type";
            case 0x12: return "Invalid parameter or invalid data";
            case 0x13: return "Address reference exists to deleted area";
            case 0x14: return "Command execution failure for unknown reason; possible PLC3 histogram overflow";
            case 0x15: return "Data conversion error";
            case 0x16: return "Scanner not able to communicate with 1771 rack adapter";
            case 0x17: return "Type mismatch";
            case 0x18: return "1771 module response was not valid";
            case 0x19: return "Duplicated label";
            case 0x1A: return "File is open; another node owns it";
            case 0x1B: return "Another node is the program owner";
            case 0x1C: return "Reserved";
            case 0x1D: return "Reserved";
            case 0x1E: return "Data table element protection violation";
            case 0x1F: return "Temporary internal problem";
            case 0x22: return "Remote rack fault";
            case 0x23: return "Timeout";
            case 0x24: return "Unknown error";
            default: return StringResources.Language.UnknownError();
        }
    }
}
