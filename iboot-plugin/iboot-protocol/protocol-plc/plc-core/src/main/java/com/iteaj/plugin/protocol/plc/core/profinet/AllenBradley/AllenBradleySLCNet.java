package com.iteaj.plugin.protocol.plc.core.profinet.AllenBradley;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.AllenBradleySLCMessage;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExThree;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.net.Socket;

public class AllenBradleySLCNet extends NetworkDeviceBase {
    /**
     * Instantiate a communication object for a Allenbradley PLC protocol
     */
    public AllenBradleySLCNet( ) {
        WordLength = 2;
        setByteTransform(new RegularByteTransform());
    }

    /**
     * Instantiate a communication object for a Allenbradley PLC protocol, default port is 44818
     * @param ipAddress PLC IpAddress
     * @param port PLC Port, default port is 44818
     */
    public AllenBradleySLCNet( String ipAddress, int port ) {
        this();
        setIpAddress(ipAddress);
        setPort(port);
    }

    protected INetMessage GetNewNetMessage( ){
        return new AllenBradleySLCMessage( );
    }

    /**
     * The current session handle, which is determined by the PLC when communicating with the PLC handshake
     */
    public int SessionHandle = 0;

    protected OperateResult InitializationOnConnect(Socket socket )
    {
        // Registering Session Information
        OperateResultExOne<byte[]> read = ReadFromCoreServer( socket,
                SoftBasic.HexStringToBytes( "01 01 00 00 00 00 00 00 00 00 00 00 00 04 00 05 00 00 00 00 00 00 00 00 00 00 00 00" ), true, true );
        if (!read.IsSuccess) return read;

        // Check the returned status

        // Extract session ID
        SessionHandle = getByteTransform().TransInt32( read.Content, 4 );

        return OperateResult.CreateSuccessResult( );
    }

    public OperateResultExOne<byte[]> Read( String address, short length ) {
        OperateResultExOne<byte[]> command = BuildReadCommand(address, length);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(command.Content));
        if (!read.IsSuccess) return read;

        OperateResultExOne<byte[]> extra = ExtraActualContent(read.Content);
        if (!extra.IsSuccess) return extra;

        return OperateResultExOne.CreateSuccessResult(extra.Content);
    }

    public OperateResult Write( String address, byte[] value ) {
        OperateResultExOne<byte[]> command = BuildWriteCommand(address, value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(command.Content));
        if (!read.IsSuccess) return read;

        OperateResultExOne<byte[]> extra = ExtraActualContent(read.Content);
        if (!extra.IsSuccess) return extra;

        return OperateResultExOne.CreateSuccessResult(extra.Content);
    }

    public OperateResultExOne<Boolean> ReadBool( String address ) {
        OperateResultExTwo<String,Integer> bitAnalysis = AnalysisBitIndex(address);
        address = bitAnalysis.Content1;
        int bitIndex = bitAnalysis.Content2;

        OperateResultExOne<byte[]> read = Read(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray(read.Content)[bitIndex]);
    }

    public OperateResult Write(String address, boolean value ) {
        OperateResultExOne<byte[]> command = BuildWriteCommand(address, value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(command.Content));
        if (!read.IsSuccess) return read;

        OperateResultExOne<byte[]> extra = ExtraActualContent(read.Content);
        if (!extra.IsSuccess) return extra;

        return OperateResultExOne.CreateSuccessResult(extra.Content);
    }

    private byte[] PackCommand( byte[] coreCmd ) {
        byte[] cmd = new byte[28 + coreCmd.length];
        cmd[0] = 0x01;
        cmd[1] = 0x07;
        cmd[2] = (byte) (coreCmd.length / 256);
        cmd[3] = (byte) (coreCmd.length % 256);
        System.arraycopy(getByteTransform().TransByte(SessionHandle), 0, cmd, 4, 4);
        System.arraycopy(coreCmd, 0, cmd, 28, coreCmd.length);
        return cmd;
    }

    /**
     * 分析地址数据信息里的位索引的信息
     * @param address 数据地址
     * @return 返回解析后的地址和位索引信息，如果这个地址使用了为地址的话
     */
    public static OperateResultExTwo<String,Integer> AnalysisBitIndex(String address )
    {
        int bitIndex = 0;
        int index = address.indexOf( '/' );
        if(index < 0) index = address.indexOf( '.' );

        if (index > 0)
        {
            bitIndex = Integer.parseInt( address.substring( index + 1 ) );
            address = address.substring( 0, index );
        }
        return OperateResultExTwo.CreateSuccessResult( address, bitIndex );
    }

    /**
     * 分析当前的地址信息，返回类型代号，区块号，起始地址<br />
     * nalyze the current address information, return the type code, block number, and actual address
     * @param address 地址信息
     * @return 结果内容对象
     */
    public static OperateResultExThree<Byte, Short, Short> AnalysisAddress(String address ) {
        if (!address.contains(":"))
            return new OperateResultExThree<Byte, Short, Short>("Address can't find ':', example : A9:0");
        String[] adds = address.split(":");

        try {
            OperateResultExThree<Byte, Short, Short> result = new OperateResultExThree<Byte, Short, Short>();
            // 还有一个没有添加，   0x8f BCD
            switch (adds[0].charAt(0)) {
                case 'A':
                    result.Content1 = (byte) 0x8E;
                    break;   // ASCII
                case 'B':
                    result.Content1 = (byte) 0x85;
                    break;   // bit
                case 'N':
                    result.Content1 = (byte) 0x89;
                    break;   // integer
                case 'F':
                    result.Content1 = (byte) 0x8A;
                    break;   // floating point
                case 'S': {
                    if (adds[0].length() > 1 && adds[0].charAt(1) == 'T')
                        result.Content1 = (byte) 0x8D;        // string
                    else
                        result.Content1 = (byte) 0x84;        // status
                    break;
                }
                case 'C':
                    result.Content1 = (byte) 0x87;
                    break;   // counter
                case 'I':
                    result.Content1 = (byte) 0x83;
                    break;   // input
                case 'O':
                    result.Content1 = (byte) 0x82;
                    break;   // output
                case 'R':
                    result.Content1 = (byte) 0x88;
                    break;   // control
                case 'T':
                    result.Content1 = (byte) 0x86;
                    break;   // timer
                case 'L':
                    result.Content1 = (byte) 0x91;
                    break;   // long integer
                default:
                    throw new Exception("Address code wrong, must be A,B,N,F,S,C,I,O,R,T,ST,L");
            }
            ;
            switch (result.Content1) {
                case (byte) 0x84:
                    result.Content2 = adds[0].length() == 1 ? (short) 2 : Short.parseShort(adds[0].substring(1));
                    break;
                case (byte) 0x82:
                    result.Content2 = adds[0].length() == 1 ? (short) 0 : Short.parseShort(adds[0].substring(1));
                    break;
                case (byte) 0x83:
                    result.Content2 = adds[0].length() == 1 ? (short) 1 : Short.parseShort(adds[0].substring(1));
                    break;
                case (byte) 0x8D:
                    result.Content2 = adds[0].length() == 2 ? (short) 1 : Short.parseShort(adds[0].substring(2));
                    break;
                default:
                    result.Content2 = Short.parseShort(adds[0].substring(1));
                    break;
            }

            result.Content3 = Short.parseShort(adds[1]);
            result.IsSuccess = true;
            result.Message = StringResources.Language.SuccessText();
            return result;

        } catch (Exception ex) {
            return new OperateResultExThree<Byte, Short, Short>("Wrong Address format: " + ex.getMessage());
        }
    }

    /**
     * 构建读取的指令信息
     * @param address 地址信息，举例：A9:0
     * @param length 读取的长度信息
     * @return 是否成功
     */
    public static OperateResultExOne<byte[]> BuildReadCommand( String address, short length ) {
        OperateResultExThree<Byte, Short, Short> analysis = AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (length < 2) length = 2;
        if (analysis.Content1 == (byte) 0x8E) {
            analysis.Content3 = (short) (analysis.Content3 / 2);
        }

        byte[] command = new byte[14];
        command[0] = 0x00;
        command[1] = 0x05;
        command[2] = 0x00;
        command[3] = 0x00;
        command[4] = 0x0F;
        command[5] = 0x00;
        command[6] = 0x00;                                                 // ID信息
        command[7] = 0x01;
        command[8] = (byte) 0xA2;
        command[9] = (byte) length;                                         // 读取字节数
        command[10] = analysis.Content2.byteValue();                       // 数据区块号
        command[11] = analysis.Content1;                                   // 数据类型号
        System.arraycopy(Utilities.getBytes(analysis.Content3), 0, command, 12, 2);  // 起始地址
        return OperateResultExOne.CreateSuccessResult(command);
    }

    /**
     * 构建写入的报文内容，变成实际的数据
     * @param address 地址信息
     * @param value 数据值
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, byte[] value ) {
        OperateResultExThree<Byte, Short, Short> analysis = AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content1 == (byte) 0x8E) {
            analysis.Content3 = (short) (analysis.Content3 / 2);
        }

        byte[] command = new byte[18 + value.length];
        command[0] = 0x00;
        command[1] = 0x05;
        command[2] = 0x00;
        command[3] = 0x00;
        command[4] = 0x0F;
        command[5] = 0x00;
        command[6] = 0x00;                                               // ID信息
        command[7] = 0x01;
        command[8] = (byte) 0xAB;
        command[9] = (byte) 0xFF;
        command[10] = Utilities.getBytes(value.length)[0];             // 写入的字节数
        command[11] = Utilities.getBytes(value.length)[1];              //
        command[12] = analysis.Content2.byteValue();                     // 数据区块号
        command[13] = analysis.Content1;                                 // 数据类型号
        System.arraycopy(Utilities.getBytes(analysis.Content3), 0, command, 14, 2); // 起始地址
        command[16] = (byte) 0xFF;
        command[17] = (byte) 0xFF;
        System.arraycopy(value, 0, command, 18, value.length);
        return OperateResultExOne.CreateSuccessResult(command);
    }

    /// <summary>
    /// 构建写入的报文内容，变成实际的数据
    /// </summary>
    /// <param name="address">地址信息</param>
    /// <param name="value">数据值</param>
    /// <returns>是否成功的结果对象</returns>
    public static OperateResultExOne<byte[]> BuildWriteCommand( String address, boolean value )
    {
        OperateResultExTwo<String,Integer> bitAnalysis = AnalysisBitIndex(address);
        address = bitAnalysis.Content1;
        int bitIndex = bitAnalysis.Content2;

        OperateResultExThree<Byte, Short, Short> analysis = AnalysisAddress( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        if (analysis.Content1 == (byte)0x8E)
        {
            analysis.Content3 = (short) (analysis.Content3 / 2);
        }

        int bitPow = 0x01 << bitIndex;

        byte[] command = new byte[20];
        command[0] = 0x00;
        command[1] = 0x05;
        command[2] = 0x00;
        command[3] = 0x00;
        command[4] = 0x0F;
        command[5] = 0x00;
        command[6] = 0x00;                                                 // ID信息
        command[7] = 0x01;
        command[8] = (byte) 0xAB;
        command[9] = (byte) 0xFF;
        command[10] = 0x02;                                                 // 写入的字节数
        command[11] = 0x00;
        command[12] = analysis.Content2.byteValue();                              // 数据区块号
        command[13] = analysis.Content1;                                    // 数据类型号
        System.arraycopy(Utilities.getBytes(analysis.Content3),0,command,14,2);  // 起始地址
        command[16] = Utilities.getBytes( bitPow )[0];
        command[17] = Utilities.getBytes( bitPow )[1];
        if (value)
        {
            command[18] = Utilities.getBytes( bitPow )[0];
            command[19] = Utilities.getBytes( bitPow )[1];
        }
        return OperateResultExOne.CreateSuccessResult( command );
    }

    /**
     * 解析当前的实际报文内容，变成数据内容
     * @param content 报文内容
     * @return 是否成功
     */
    public static OperateResultExOne<byte[]> ExtraActualContent(byte[] content ) {
        if (content.length < 36) {
            return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + SoftBasic.ByteToHexString(content, ' '));
        } else {
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 36));
        }
    }

}
