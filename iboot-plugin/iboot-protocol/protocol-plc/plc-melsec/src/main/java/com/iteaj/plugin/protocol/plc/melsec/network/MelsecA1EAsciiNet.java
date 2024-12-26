package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.MelsecA1EAsciiMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * 三菱PLC通讯协议，采用A兼容1E帧协议实现，使用ASCII码通讯，请根据实际型号来进行选取<br />
 * Mitsubishi PLC communication protocol, implemented using A compatible 1E frame protocol, using ascii code communication, please choose according to the actual model
 */
public class MelsecA1EAsciiNet extends NetworkDeviceBase {
    /**
     * 实例化三菱的A兼容1E帧协议的通讯对象
     */
    public MelsecA1EAsciiNet() {
        this.WordLength = 1;
        this.LogMsgFormatBinary  = false;
        this.setByteTransform(new RegularByteTransform());
    }

    /**
     * 实例化一个三菱的A兼容1E帧协议的通讯对象
     *
     * @param ipAddress PLCd的Ip地址
     * @param port      PLC的端口
     */
    public MelsecA1EAsciiNet(String ipAddress, int port) {
        this();
        super.setIpAddress(ipAddress);
        super.setPort(port);
    }


    @Override
    protected INetMessage GetNewNetMessage() {
        return new MelsecA1EAsciiMessage( );
    }

    private byte PLCNumber = (byte) (0xFF);                       // PLC编号

    /**
     * 获取PLC编号
     *
     * @return PLC编号
     */
    public byte getPLCNumber() {
        return PLCNumber;
    }

    /**
     * 设置PLC编号
     *
     * @param plcNumber PLC编号
     */
    public void setPLCNumber(byte plcNumber) {
        PLCNumber = plcNumber;
    }

    public OperateResultExOne<byte[]> Read(String address, short length ) {
        // 获取指令
        OperateResultExOne<ArrayList<byte[]>> command = BuildReadCommand(address, length, false, PLCNumber);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> array = new ArrayList();
        for (int i = 0; i < command.Content.size(); i++) {
            // 核心交互
            OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 错误代码验证
            OperateResult check = CheckResponseLegal(read.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            // 数据解析，需要传入是否使用位的参数
            OperateResultExOne<byte[]> extra = ExtractActualData(read.Content, false);
            if (!extra.IsSuccess) return extra;

            Utilities.ArrayListAddArray(array, extra.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(array));
    }

    public OperateResult Write( String address, byte[] value )
    {
        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteWordCommand( address, value, PLCNumber );
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( command.Content );
        if (!read.IsSuccess) return read;

        // 错误码校验 (在A兼容1E协议中，结束代码后面紧跟的是异常信息的代码)
        OperateResult check = CheckResponseLegal( read.Content );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        // 成功
        return OperateResult.CreateSuccessResult( );
    }

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        // 获取指令
        OperateResultExOne<ArrayList<byte[]>> command = BuildReadCommand(address, length, true, PLCNumber);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> array = new ArrayList<>();
        for (int i = 0; i < command.Content.size(); i++) {
            // 核心交互
            OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 错误代码验证
            OperateResult check = CheckResponseLegal(read.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            // 数据解析，需要传入是否使用位的参数
            OperateResultExOne<byte[]> extract = ExtractActualData(read.Content, true);
            if (!extract.IsSuccess) return OperateResultExOne.CreateFailedResult(extract);

            // 转化bool数组
            Utilities.ArrayListAddArray(array, extract.Content);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.getBoolArray(array, length));
    }

    public OperateResult Write( String address, boolean[] values ) {
        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteBoolCommand(address, values, PLCNumber);
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 错误码校验 (在A兼容1E协议中，结束代码后面紧跟的是异常信息的代码)
        return CheckResponseLegal(read.Content);
    }

    // region Static Method Helper

    /**
     * 根据类型地址长度确认需要读取的指令头
     * @param address 起始地址
     * @param length 长度
     * @param isBit 指示是否按照位成批的读出
     * @param plcNumber PLC编号
     * @return 带有成功标志的指令数据
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand( String address, short length, boolean isBit, byte plcNumber )
    {
        OperateResultExTwo<MelsecA1EDataType, Integer> analysis = MelsecHelper.McA1EAnalysisAddress( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        // 默认信息----注意：高低字节交错
        // byte subtitle = analysis.Content1.DataType == 0x01 ? (byte)0x00 : (byte)0x01;
        byte subtitle = isBit ? (byte)0x00 : (byte)0x01;
        int[] splits = SoftBasic.SplitIntegerToArray( length, isBit ? 256 : 64 );
        ArrayList<byte[]> array = new ArrayList<>( );

        for (int i = 0; i < splits.length; i++) {

            byte[] _PLCCommand = new byte[24];
            _PLCCommand[ 0] = SoftBasic.BuildAsciiBytesFrom(subtitle)[0];                // 副标题
            _PLCCommand[ 1] = SoftBasic.BuildAsciiBytesFrom(subtitle)[1];
            _PLCCommand[ 2] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[0];               // PLC号
            _PLCCommand[ 3] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[1];
            _PLCCommand[ 4] = 0x30;                                                        // 监视定时器，10*250ms=2.5秒
            _PLCCommand[ 5] = 0x30;
            _PLCCommand[ 6] = 0x30;
            _PLCCommand[ 7] = 0x41;
            _PLCCommand[ 8] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content1.getDataCode())[1])[0];
            _PLCCommand[ 9] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content1.getDataCode())[1])[1];
            _PLCCommand[10] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content1.getDataCode())[0])[0];
            _PLCCommand[11] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content1.getDataCode())[0])[1];
            _PLCCommand[12] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[3])[0];
            _PLCCommand[13] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[3])[1];
            _PLCCommand[14] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[2])[0];
            _PLCCommand[15] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[2])[1];
            _PLCCommand[16] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[1])[0];
            _PLCCommand[17] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[1])[1];
            _PLCCommand[18] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[0])[0];
            _PLCCommand[19] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[0])[1];

            int len = splits[i];
            if (len == 256) len = 0x00;
            _PLCCommand[20] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(len % 256)[0])[0];
            _PLCCommand[21] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(len % 256)[0])[1];
            _PLCCommand[22] = 0x30;
            _PLCCommand[23] = 0x30;

            array.add(_PLCCommand);
            analysis.Content2 += splits[i];
        }
        return OperateResultExOne.CreateSuccessResult( array );
    }

    /**
     * 根据类型地址以及需要写入的数据来生成指令头
     * @param address 起始地址
     * @param value 数据值
     * @param plcNumber PLC编号
     * @return 带有成功标志的指令数据
     */
    public static OperateResultExOne<byte[]> BuildWriteWordCommand( String address, byte[] value, byte plcNumber )
    {
        OperateResultExTwo<MelsecA1EDataType, Integer> analysis = MelsecHelper.McA1EAnalysisAddress( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        value = MelsecHelper.TransByteArrayToAsciiByteArray( value );
        byte[] _PLCCommand = new byte[24 + value.length];
        _PLCCommand[ 0] = 0x30;                                                       // 副标题
        _PLCCommand[ 1] = 0x33;
        _PLCCommand[ 2] = SoftBasic.BuildAsciiBytesFrom( plcNumber )[0];               // PLC号
        _PLCCommand[ 3] = SoftBasic.BuildAsciiBytesFrom( plcNumber )[1];
        _PLCCommand[ 4] = 0x30;                                                        // 监视定时器，10*250ms=2.5秒
        _PLCCommand[ 5] = 0x30;
        _PLCCommand[ 6] = 0x30;
        _PLCCommand[ 7] = 0x41;
        _PLCCommand[ 8] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content1.getDataCode() )[1] )[0];
        _PLCCommand[ 9] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content1.getDataCode() )[1] )[1];
        _PLCCommand[10] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content1.getDataCode() )[0] )[0];
        _PLCCommand[11] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content1.getDataCode() )[0] )[1];
        _PLCCommand[12] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content2 )[3] )[0];
        _PLCCommand[13] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content2 )[3] )[1];
        _PLCCommand[14] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content2 )[2] )[0];
        _PLCCommand[15] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content2 )[2] )[1];
        _PLCCommand[16] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content2 )[1] )[0];
        _PLCCommand[17] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content2 )[1] )[1];
        _PLCCommand[18] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content2 )[0] )[0];
        _PLCCommand[19] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( analysis.Content2 )[0] )[1];
        _PLCCommand[20] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( value.length / 4 )[0] )[0];
        _PLCCommand[21] = SoftBasic.BuildAsciiBytesFrom( Utilities.getBytes( value.length / 4 )[0] )[1];
        _PLCCommand[22] = 0x30;
        _PLCCommand[23] = 0x30;
        System.arraycopy(value, 0, _PLCCommand, 24, value.length);
        return OperateResultExOne.CreateSuccessResult( _PLCCommand );
    }

    /**
     * 根据类型地址以及需要写入的数据来生成指令头
     * @param address 起始地址
     * @param value 数据值
     * @param plcNumber PLC编号
     * @return 带有成功标志的指令数据
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolCommand( String address, boolean[] value, byte plcNumber ) {
        OperateResultExTwo<MelsecA1EDataType, Integer> analysis = MelsecHelper.McA1EAnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] buffer = value.length % 2 == 1 ? new byte[value.length + 1] : new byte[value.length];
        for (int i = 0; i < buffer.length; i++) {
            if (i < value.length && value[i])
                buffer[i] = 0x31;
            else
                buffer[i] = 0x30;
        }

        byte[] _PLCCommand = new byte[24 + buffer.length];
        _PLCCommand[ 0] = 0x30;                                                       // 副标题
        _PLCCommand[ 1] = 0x32;
        _PLCCommand[ 2] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[0];               // PLC号
        _PLCCommand[ 3] = SoftBasic.BuildAsciiBytesFrom(plcNumber)[1];
        _PLCCommand[ 4] = 0x30;                                                        // 监视定时器，10*250ms=2.5秒
        _PLCCommand[ 5] = 0x30;
        _PLCCommand[ 6] = 0x30;
        _PLCCommand[ 7] = 0x41;
        _PLCCommand[ 8] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content1.getDataCode())[1])[0];
        _PLCCommand[ 9] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content1.getDataCode())[1])[1];
        _PLCCommand[10] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content1.getDataCode())[0])[0];
        _PLCCommand[11] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content1.getDataCode())[0])[1];
        _PLCCommand[12] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[3])[0];
        _PLCCommand[13] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[3])[1];
        _PLCCommand[14] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[2])[0];
        _PLCCommand[15] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[2])[1];
        _PLCCommand[16] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[1])[0];
        _PLCCommand[17] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[1])[1];
        _PLCCommand[18] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[0])[0];
        _PLCCommand[19] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(analysis.Content2)[0])[1];
        _PLCCommand[20] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(value.length)[0])[0];
        _PLCCommand[21] = SoftBasic.BuildAsciiBytesFrom(Utilities.getBytes(value.length)[0])[1];
        _PLCCommand[22] = 0x30;
        _PLCCommand[23] = 0x30;
        System.arraycopy(buffer, 0, _PLCCommand, 24, buffer.length);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 检测反馈的消息是否合法
     * @param response 接收的报文
     * @return 是否成功
     */
    public static OperateResult CheckResponseLegal(byte[] response ) {
        if (response.length < 4) return new OperateResult(StringResources.Language.ReceiveDataLengthTooShort());
        if (response[2] == 0x30 && response[3] == 0x30) return OperateResult.CreateSuccessResult();
        if (response[2] == 0x35 && response[3] == 0x42) return new OperateResult(Integer.parseInt(
                new String(response, 4, 2, StandardCharsets.US_ASCII), 16), StringResources.Language.MelsecPleaseReferToManualDocument());
        return new OperateResult(Integer.parseInt(new String(response, 2, 2, StandardCharsets.US_ASCII), 16),
                StringResources.Language.MelsecPleaseReferToManualDocument());
    }

    /**
     * 从PLC反馈的数据中提取出实际的数据内容，需要传入反馈数据，是否位读取
     * @param response 反馈的数据内容
     * @param isBit 是否位读取
     * @return 解析后的结果对象
     */
    public static OperateResultExOne<byte[]> ExtractActualData( byte[] response, boolean isBit ) {
        if (isBit) {
            byte[] buffer = new byte[response.length - 4];
            for (int i = 0; i < buffer.length; i++) {
                if (response[i + 4] == 0x30)
                    buffer[i] = 0x00;
                else
                    buffer[i] = 0x01;
            }
            return OperateResultExOne.CreateSuccessResult(buffer);
        } else
            return OperateResultExOne.CreateSuccessResult(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(response, 4)));
    }

    //endregion
}
