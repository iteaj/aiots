package com.iteaj.plugin.protocol.plc.core.profinet.melsec.helper;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.address.McAddressData;
import com.iteaj.plugin.protocol.plc.core.net.ReadWriteNetHelper;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.profinet.melsec.MelsecA3CNetOverTcp;
import com.iteaj.plugin.protocol.plc.core.profinet.melsec.MelsecHelper;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MelsecA3CNetHelper {

    public static byte[] PackCommand(MelsecA3CNetOverTcp plc, byte[] mcCommand ){
        return PackCommand(plc,mcCommand, (byte) 0);
    }

    /**
     * 将命令进行打包传送，可选站号及是否和校验机制
     * @param plc PLC设备通信对象
     * @param mcCommand mc协议的命令
     * @param station PLC的站号
     * @return 最终的原始报文信息
     */
    public static byte[] PackCommand(MelsecA3CNetOverTcp plc, byte[] mcCommand, byte station ) {
        MemoryStream ms = new MemoryStream();
        if (plc.Format != 3)
            ms.WriteByte(AsciiControl.ENQ);                           // STX
        else
            ms.WriteByte(AsciiControl.STX);
        if (plc.Format == 2) {
            ms.WriteByte(0x30);                                       // 格式二的块编号
            ms.WriteByte(0x30);
        }
        ms.WriteByte(0x46);                                           // 帧识别F9
        ms.WriteByte(0x39);
        ms.WriteByte(SoftBasic.BuildAsciiBytesFrom(station)[0]);    // 站号
        ms.WriteByte(SoftBasic.BuildAsciiBytesFrom(station)[1]);
        ms.WriteByte(0x30);                                           // 网络编号
        ms.WriteByte(0x30);
        ms.WriteByte(0x46);                                           // 可编程控制器编号
        ms.WriteByte(0x46);
        ms.WriteByte(0x30);                                           // 本站编号
        ms.WriteByte(0x30);
        ms.Write(mcCommand, 0, mcCommand.length);
        if (plc.Format == 3) {
            ms.WriteByte(AsciiControl.ETX);
        }
        // 计算和校验
        if (plc.SumCheck) {
            byte[] cmd = ms.ToArray();
            int sum = 0;
            for (int i = 1; i < cmd.length; i++) {
                sum += cmd[i];
            }
            ms.WriteByte(SoftBasic.BuildAsciiBytesFrom((byte) sum)[0]);
            ms.WriteByte(SoftBasic.BuildAsciiBytesFrom((byte) sum)[1]);
        }
        if (plc.Format == 4) {
            ms.WriteByte(AsciiControl.CR);    // CR
            ms.WriteByte(AsciiControl.LF);    // LF
        }
        byte[] buffer = ms.ToArray();
        return buffer;
    }

    private static int GetErrorCodeOrDataStartIndex( MelsecA3CNetOverTcp plc ) {
        int start = 11;
        switch (plc.Format) {
            case 1:
                start = 11;
                break;
            case 2:
                start = 13;
                break;
            case 3:
                start = 15;
                break;
            case 4:
                start = 11;
                break;
        }
        return start;
    }

    /**
     * 根据PLC返回的数据信息，获取到实际的数据内容
     * @param plc PLC设备通信对象
     * @param response PLC返回的数据信息
     * @return 带有是否成功的读取结果对象内容
     */
    public static OperateResultExOne<byte[]> ExtraReadActualResponse(MelsecA3CNetOverTcp plc, byte[] response ) {
        try {
            int start = GetErrorCodeOrDataStartIndex(plc);
            // 结果验证
            if (plc.Format == 1 || plc.Format == 2 || plc.Format == 4) {
                if (response[0] == AsciiControl.NAK) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne<byte[]>(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (response[0] != AsciiControl.STX)
                    return new OperateResultExOne<byte[]>(response[0], "Read Faild:" + SoftBasic.GetAsciiStringRender(response));
            } else if (plc.Format == 3) {
                String ending = new String(response, 11, 4, StandardCharsets.US_ASCII);
                if (ending.equals("QNAK")) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne<byte[]>(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (!ending.equals("QACK"))
                    return new OperateResultExOne<byte[]>(response[0], "Read Faild:" + SoftBasic.GetAsciiStringRender(response));
            }

            int end = -1;
            for (int i = start; i < response.length; i++) {
                if (response[i] == AsciiControl.ETX) {
                    end = i;
                    break;
                }
            }
            if (end == -1) end = response.length;
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle(response, start, end - start));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("ExtraReadActualResponse Wrong:" + ex.getMessage() +
                    "\r\nSource: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    private static OperateResult CheckWriteResponse(MelsecA3CNetOverTcp plc, byte[] response ) {
        int start = GetErrorCodeOrDataStartIndex(plc);

        try {
            // 结果验证
            if (plc.Format == 1 || plc.Format == 2) {
                if (response[0] == AsciiControl.NAK) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne<byte[]>(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (response[0] != AsciiControl.ACK)
                    return new OperateResultExOne<byte[]>(response[0], "Write Faild:" + SoftBasic.GetAsciiStringRender(response));
            } else if (plc.Format == 3) {
                if (response[0] != AsciiControl.STX)
                    return new OperateResultExOne<byte[]>(response[0], "Write Faild:" + SoftBasic.GetAsciiStringRender(response));
                String ending = new String(response, 11, 4, StandardCharsets.US_ASCII);
                if (ending.equals("QNAK")) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne<byte[]>(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (!ending.equals("QACK"))
                    return new OperateResultExOne<byte[]>(response[0], "Write Faild:" + SoftBasic.GetAsciiStringRender(response));
            } else if (plc.Format == 4) {
                if (response[0] == AsciiControl.NAK) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne<byte[]>(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (response[0] != AsciiControl.ACK)
                    return new OperateResultExOne<byte[]>(response[0], "Write Faild:" + SoftBasic.GetAsciiStringRender(response));
            }
            return OperateResult.CreateSuccessResult();
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("CheckWriteResponse failed: " + ex.getMessage() + "\r\nContent: " + SoftBasic.GetAsciiStringRender(response));
        }
    }

		//region Static Helper

    /**
     * 批量读取PLC的数据，以字为单位，支持读取X,Y,M,S,D,T,C，具体的地址范围需要根据PLC型号来确认
     * @param plc PLC设备通信对象
     * @param address 地址信息
     * @param length 数据长度
     * @return 读取结果信息
     */
    public static OperateResultExOne<byte[]> Read( MelsecA3CNetOverTcp plc, String address, short length ) {
        byte stat = plc.Station;
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
            address = extraPara.Content2;
        }
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address, length, false);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        ArrayList<Byte> bytesContent = new ArrayList<Byte>();
        int alreadyFinished = 0;
        while (alreadyFinished < length) {
            short readLength = (short) Math.min(length - alreadyFinished, McHelper.GetReadWordLength(McType.MCAscii));
            addressResult.Content.setLength(readLength);

            // 解析指令
            byte[] command = McAsciiHelper.BuildAsciiReadMcCoreCommand(addressResult.Content, false);

            // 核心交互
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(PackCommand(plc, command, stat));
            if (!read.IsSuccess) return read;

            // 结果验证
            OperateResultExOne<byte[]> check = ExtraReadActualResponse(plc, read.Content);
            if (!check.IsSuccess) return check;

            Utilities.ArrayListAddArray(bytesContent, MelsecHelper.TransAsciiByteArrayToByteArray(check.Content));
            alreadyFinished += readLength;

            // 字的话就是正常的偏移位置，如果是位的话，就转到位的数据
            if (addressResult.Content.getMcDataType().getDataType() == 0)
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength);
            else
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength * 16);
        }
        // 提取结果
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(bytesContent));
    }


    /**
     * 批量写入PLC的数据，以字为单位，也就是说最少2个字节信息，支持X,Y,M,S,D,T,C，具体的地址范围需要根据PLC型号来确认
     * @param plc PLC设备通信对象
     * @param address 地址信息
     * @param value 数据值
     * @return 是否写入成功
     */
    public static OperateResult Write( MelsecA3CNetOverTcp plc, String address, byte[] value ) {
        byte stat = plc.Station;
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
            address = extraPara.Content2;
        }
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address, 0, false);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        // 解析指令
        byte[] command = McAsciiHelper.BuildAsciiWriteWordCoreCommand(addressResult.Content, value);

        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(PackCommand(plc, command, stat));
        if (!read.IsSuccess) return read;

        // 结果验证
        return CheckWriteResponse(plc, read.Content);
    }

    /**
     * 批量读取bool类型数据，支持的类型为X,Y,S,T,C，具体的地址范围取决于PLC的类型
     * @param plc PLC设备通信对象
     * @param address 地址信息，比如X10,Y17，注意X，Y的地址是8进制的
     * @param length 读取的长度
     * @return 读取结果信息
     */
    public static OperateResultExOne<boolean[]> ReadBool( MelsecA3CNetOverTcp plc, String address, short length ) {
        byte stat = plc.Station;
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
            address = extraPara.Content2;
        }
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address, length, true);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        ArrayList<Boolean> boolContent = new ArrayList<Boolean>();
        int alreadyFinished = 0;
        while (alreadyFinished < length) {
            short readLength = (short) Math.min(length - alreadyFinished, McHelper.GetReadBoolLength(McType.MCAscii));
            addressResult.Content.setLength(readLength);

            // 获取指令
            byte[] command = McAsciiHelper.BuildAsciiReadMcCoreCommand(addressResult.Content, true);

            // 核心交互
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(PackCommand(plc, command, stat));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 结果验证
            OperateResultExOne<byte[]> check = ExtraReadActualResponse(plc, read.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            // 提取结果
            Utilities.ArrayListAddArray(boolContent, Utilities.getBoolArray(check.Content, (byte) 0x30));
            alreadyFinished += readLength;

            addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(boolContent));
    }

    /**
     * @see MelsecA3CNetOverTcp#Write(String, boolean[])
     */
    public static OperateResult Write( MelsecA3CNetOverTcp plc, String address, boolean[] value ) {
        if (plc.EnableWriteBitToWordRegister && address.contains(".")) {
            return ReadWriteNetHelper.WriteBoolWithWord(plc, address, value, 16);
        } else {
            byte stat = plc.Station;
            OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(address, "s");
            if (extraPara.IsSuccess) {
                stat = extraPara.Content1.byteValue();
                address = extraPara.Content2;
            }

            // 分析地址
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address, (short) 0, true);
            if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

            // 解析指令
            byte[] command = McAsciiHelper.BuildAsciiWriteBitCoreCommand(addressResult.Content, value);

            // 核心交互
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(PackCommand(plc, command, stat));
            if (!read.IsSuccess) return read;

            // 结果验证
            return CheckWriteResponse(plc, read.Content);
        }
    }

    /**
     * 远程Run操作
     * @param plc PLC设备通信对象
     * @return 是否成功
     */
    public static OperateResult RemoteRun( MelsecA3CNetOverTcp plc ) {
        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(PackCommand(plc, "1001000000010000".getBytes(StandardCharsets.US_ASCII), plc.Station));
        if (!read.IsSuccess) return read;

        // 结果验证
        return CheckWriteResponse(plc, read.Content);
    }

    /**
     * 远程Stop操作
     * @param plc PLC设备通信对象
     * @return 是否成功
     */
    public static OperateResult RemoteStop( MelsecA3CNetOverTcp plc ) {
        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(PackCommand(plc, "100200000001".getBytes(StandardCharsets.US_ASCII), plc.Station));
        if (!read.IsSuccess) return read;

        // 结果验证
        return CheckWriteResponse(plc, read.Content);
    }

    /**
     * 读取PLC的型号信息
     * @param plc PLC设备通信对象
     * @return 返回型号的结果对象
     */
    public static OperateResultExOne<String> ReadPlcType( MelsecA3CNetOverTcp plc ) {
        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(PackCommand(plc, "01010000".getBytes(StandardCharsets.US_ASCII), plc.Station));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 结果验证
        OperateResultExOne<byte[]> check = ExtraReadActualResponse(plc, read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 成功
        return OperateResultExOne.CreateSuccessResult(new String(check.Content, 0, 16, StandardCharsets.US_ASCII).trim());
    }

}
