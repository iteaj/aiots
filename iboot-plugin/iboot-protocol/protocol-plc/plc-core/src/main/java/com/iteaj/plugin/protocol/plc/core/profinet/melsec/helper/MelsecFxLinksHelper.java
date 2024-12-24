package com.iteaj.plugin.protocol.plc.core.profinet.melsec.helper;


import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.address.MelsecFxLinksAddress;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.profinet.melsec.MelsecFxLinksOverTcp;
import com.iteaj.plugin.protocol.plc.core.serial.SoftLRC;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MelsecFxLinksHelper {
    /**
     * 将当前的报文进行打包，根据和校验的方式以及格式信息来实现打包操作
     * @param plc PLC通信对象
     * @param command 原始的命令数据
     * @return 打包后的命令
     */
    public  static byte[] PackCommandWithHeader(MelsecFxLinksOverTcp plc, byte[] command ) {
        // 如果发现报文已经封包好了，就不再进行处理
        if (command.length > 3 && command[0] == AsciiControl.ENQ) return command;

        byte[] core = command;
        if (plc.getSumCheck()) {
            core = new byte[command.length + 2];
            Utilities.ByteArrayCopyTo(command, core, 0);
            SoftLRC.CalculateAccAndFill(core, 0, 2);
        }

        if (plc.getFormat() == 1)
            return SoftBasic.SpliceArray(new byte[]{AsciiControl.ENQ}, core);
        else if (plc.getFormat() == 4)
            return SoftBasic.SpliceArray(new byte[]{AsciiControl.ENQ}, core, new byte[]{AsciiControl.CR, AsciiControl.LF});
        return SoftBasic.SpliceArray(new byte[]{AsciiControl.ENQ}, core);
    }

    /**
     * 创建一条读取的指令信息，需要指定一些参数
     * @param station PLC的站号
     * @param address 地址信息
     * @param length 数据长度
     * @param isBool 是否位读取
     * @param waitTime 等待时间
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(byte station, String address, short length, boolean isBool, byte waitTime ) {
        OperateResultExOne<MelsecFxLinksAddress> addressAnalysis = MelsecFxLinksAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        int[] lens = SoftBasic.SplitIntegerToArray(length, isBool ? 256 : 64);
        ArrayList<byte[]> list = new ArrayList<>();
        for (int i = 0; i < lens.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%02X", station));
            stringBuilder.append("FF");

            if (isBool)
                stringBuilder.append("BR");
            else {
                if (addressAnalysis.Content.getAddressStart() >= 10000)
                    stringBuilder.append("QR");
                else
                    stringBuilder.append("WR");
            }

            stringBuilder.append(String.format("%X", waitTime));
            stringBuilder.append(addressAnalysis.Content.toString());
            // 如果长度为256则使用00来表示
            if (lens[i] == 256)
                stringBuilder.append("00");
            else
                stringBuilder.append(String.format("%02X", lens[i]));

            list.add(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
            addressAnalysis.Content.setAddressStart(addressAnalysis.Content.getAddressStart() + lens[i]);
        }

        return OperateResultExOne.CreateSuccessResult(list);
    }

    /**
     * 创建一条别入bool数据的指令信息，需要指定一些参数
     * @param station 站号
     * @param address 地址
     * @param value 数组值
     * @param waitTime 等待时间
     * @return 是否创建成功
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(byte station, String address, boolean[] value, byte waitTime ) {
        OperateResultExOne<MelsecFxLinksAddress> addressAnalysis = MelsecFxLinksAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        stringBuilder.append("BW");
        stringBuilder.append(String.format("%X", waitTime));
        stringBuilder.append(addressAnalysis.Content.toString());
        stringBuilder.append(String.format("%02X", value.length));
        for (int i = 0; i < value.length; i++) {
            stringBuilder.append(value[i] ? "1" : "0");
        }

        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 创建一条别入byte数据的指令信息，需要指定一些参数，按照字单位
     * @param station 站号
     * @param address 地址
     * @param value 数组值
     * @param waitTime 等待时间
     * @return 命令报文的结果内容对象
     */
    public static OperateResultExOne<byte[]> BuildWriteByteCommand( byte station, String address, byte[] value, byte waitTime ) {
        OperateResultExOne<MelsecFxLinksAddress> addressAnalysis = MelsecFxLinksAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        if (addressAnalysis.Content.getAddressStart() >= 10000)
            stringBuilder.append("QW");
        else
            stringBuilder.append("WW");
        stringBuilder.append(String.format("%X", waitTime));
        stringBuilder.append(addressAnalysis.Content.toString());
        stringBuilder.append(String.format("%02X", (value.length / 2)));

        // 字写入
        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length / 2; i++) {
            Utilities.ByteArrayCopyTo(SoftBasic.BuildAsciiBytesFrom((short) BitConverter.ToUInt16(value, i * 2)), buffer, 4 * i);
        }
        stringBuilder.append(new String(buffer, StandardCharsets.US_ASCII));
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 创建启动PLC的报文信息
     * @param station 站号信息
     * @param waitTime 等待时间
     * @return 命令报文的结果内容对象
     */
    public static OperateResultExOne<byte[]> BuildStart( byte station, byte waitTime ) {


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        stringBuilder.append("RR");
        stringBuilder.append(String.format("%X", waitTime));
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 创建启动PLC的报文信息
     * @param station 站号信息
     * @param waitTime 等待时间
     * @return 命令报文的结果内容对象
     */
    public static OperateResultExOne<byte[]> BuildStop( byte station, byte waitTime ) {


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        stringBuilder.append("RS");
        stringBuilder.append(String.format("%X", waitTime));

        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 创建读取PLC类型的命令报文
     * @param station 站号信息
     * @param waitTime 等待实际
     * @return 命令报文的结果内容对象
     */
    public static OperateResultExOne<byte[]> BuildReadPlcType( byte station, byte waitTime ) {


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("FF");
        stringBuilder.append("PC");
        stringBuilder.append(String.format("%X", waitTime));

        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 从编码中提取PLC的型号信息
     * @param code 编码
     * @return PLC的型号信息
     */
    public static OperateResultExOne<String> GetPlcTypeFromCode( String code ) {
        switch (code) {
            case "F2":
                return OperateResultExOne.CreateSuccessResult("FX1S");
            case "8E":
                return OperateResultExOne.CreateSuccessResult("FX0N");
            case "8D":
                return OperateResultExOne.CreateSuccessResult("FX2/FX2C");
            case "9E":
                return OperateResultExOne.CreateSuccessResult("FX1N/FX1NC");
            case "9D":
                return OperateResultExOne.CreateSuccessResult("FX2N/FX2NC");
            case "F4":
                return OperateResultExOne.CreateSuccessResult("FX3G");
            case "F3":
                return OperateResultExOne.CreateSuccessResult("FX3U/FX3UC");
            case "98":
                return OperateResultExOne.CreateSuccessResult("A0J2HCPU");
            case "A1":
                return OperateResultExOne.CreateSuccessResult("A1CPU /A1NCPU");
            // case "98": return OperateResultExOne.CreateSuccessResult( "A1SCPU/A1SJCPU" );
            case "A2":
                return OperateResultExOne.CreateSuccessResult("A2CPU/A2NCPU/A2SCPU");
            case "92":
                return OperateResultExOne.CreateSuccessResult("A2ACPU");
            case "93":
                return OperateResultExOne.CreateSuccessResult("A2ACPU-S1");
            case "9A":
                return OperateResultExOne.CreateSuccessResult("A2CCPU");
            case "82":
                return OperateResultExOne.CreateSuccessResult("A2USCPU");
            case "83":
                return OperateResultExOne.CreateSuccessResult("A2CPU-S1/A2USCPU-S1");
            case "A3":
                return OperateResultExOne.CreateSuccessResult("A3CPU/A3NCPU");
            case "94":
                return OperateResultExOne.CreateSuccessResult("A3ACPU");
            case "A4":
                return OperateResultExOne.CreateSuccessResult("A3HCPU/A3MCPU");
            case "84":
                return OperateResultExOne.CreateSuccessResult("A3UCPU");
            case "85":
                return OperateResultExOne.CreateSuccessResult("A4UCPU");
            // case "9A": return OperateResultExOne.CreateSuccessResult( "A52GCPU" );
            // case "A3": return OperateResultExOne.CreateSuccessResult( "A73CPU" );
            // case "A3": return OperateResultExOne.CreateSuccessResult( "A7LMS-F" );
            case "AB":
                return OperateResultExOne.CreateSuccessResult("AJ72P25/R25");
            case "8B":
                return OperateResultExOne.CreateSuccessResult("AJ72LP25/BR15");
            default:
                return new OperateResultExOne(StringResources.Language.NotSupportedDataType() + " Code:" + code);
        }
    }

    private static String GetErrorText( int error ) {
        switch (error) {
            case 0x02:
                return StringResources.Language.MelsecFxLinksError02();
            case 0x03:
                return StringResources.Language.MelsecFxLinksError03();
            case 0x06:
                return StringResources.Language.MelsecFxLinksError06();
            case 0x07:
                return StringResources.Language.MelsecFxLinksError07();
            case 0x0A:
                return StringResources.Language.MelsecFxLinksError0A();
            case 0x10:
                return StringResources.Language.MelsecFxLinksError10();
            case 0x18:
                return StringResources.Language.MelsecFxLinksError18();
            default:
                return StringResources.Language.UnknownError();
        }
    }

    /**
     * 检查PLC的消息反馈是否合法，合法则提取当前的数据信息，当时写入的命令消息时，无任何的数据返回<br />
     * Check whether the PLC's message feedback is legal. If it is legal, extract the current data information. When the command message is written at that time, no data is returned.
     * @param response 从PLC反馈的数据消息
     * @return 检查的结果消息
     */
    public static OperateResultExOne<byte[]> CheckPlcResponse( byte[] response ) {
        try {
            if (response[0] == AsciiControl.NAK) {
                int err = Convert.ToInt32(new String(response, 5, 2, StandardCharsets.US_ASCII), 16);
                return new OperateResultExOne<byte[]>(err, GetErrorText(err));
            }
            if ((response[0] != AsciiControl.STX) && (response[0] != AsciiControl.ACK))
                return new OperateResultExOne<byte[]>(response[0], "Check command failed: " + SoftBasic.GetAsciiStringRender(response));

            if (response[0] == AsciiControl.ACK) return OperateResultExOne.CreateSuccessResult(new byte[0]);
            int etxIndex = -1;
            for (int i = 5; i < response.length; i++) {
                if (response[i] == AsciiControl.ETX) {
                    etxIndex = i;
                    break;
                }
            }
            if (etxIndex == -1) etxIndex = response.length;
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle(response, 5, etxIndex - 5));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Check Plc Response failed Error: " + ex.getMessage() + " Source: " + SoftBasic.GetAsciiStringRender(response));
        }
    }


    private static OperateResultExOne<byte[]> ExtraResponse( byte[] response ) {
        try {
            byte[] content = new byte[response.length / 2];
            for (int i = 0; i < content.length / 2; i++) {
                int tmp = Convert.ToInt32(new String(response, i * 4, 4, StandardCharsets.US_ASCII), 16);
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short) tmp), content, i * 2);
            }
            return OperateResultExOne.CreateSuccessResult(content);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Extra source data failed: " + ex.getMessage() + "\r\nSource: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    /**
     * 批量读取PLC的数据，以字为单位，支持读取X,Y,M,S,D,T,C，具体的地址范围需要根据PLC型号来确认，地址支持动态指定站号，例如：s=2;D100<br />
     * Read PLC data in batches, in units of words, supports reading X, Y, M, S, D, T, C. The specific address range needs to be confirmed according to the PLC model,
     * The address supports dynamically specifying the station number, for example: s=2;D100
     * @param plc PLC通信对象
     * @param address 地址信息
     * @param length 数据长度
     * @return 读取结果信息
     */
    public static OperateResultExOne<byte[]> Read( MelsecFxLinksOverTcp plc, String address, short length ) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
            address = extraPara.Content2;
        }

        // 解析指令
        OperateResultExOne<ArrayList<byte[]>> command = MelsecFxLinksHelper.BuildReadCommand(stat, address, length, false, plc.getWaittingTime());
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> result = new ArrayList<>();
        for (int j = 0; j < command.Content.size(); j++) {
            // 核心交互
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(command.Content.get(j));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 结果验证
            OperateResultExOne<byte[]> extra = CheckPlcResponse(read.Content);
            if (!extra.IsSuccess) return extra;

            // 提取结果
            OperateResultExOne<byte[]> content = ExtraResponse(extra.Content);
            if (!content.IsSuccess) return content;

            Utilities.ArrayListAddArray(result, content.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(result));
    }

    /**
     * 批量写入PLC的数据，以字为单位，也就是说最少2个字节信息，支持X,Y,M,S,D,T,C，具体的地址范围需要根据PLC型号来确认，地址支持动态指定站号，例如：s=2;D100<br />
     * The data written to the PLC in batches is in units of words, that is, at least 2 bytes of information.
     * It supports X, Y, M, S, D, T, and C. The specific address range needs to be confirmed according to the PLC model,
     * The address supports dynamically specifying the station number, for example: s=2;D100
     * @param plc PLC通信对象
     * @param address 地址信息
     * @param value 数据值
     * @return 是否写入成功
     */
    public static OperateResult Write(MelsecFxLinksOverTcp plc, String address, byte[] value ) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
            address = extraPara.Content2;
        }

        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteByteCommand(stat, address, value, plc.getWaittingTime());
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        OperateResultExOne<byte[]> extra = CheckPlcResponse(read.Content);
        if (!extra.IsSuccess) return extra;

        // 提取结果
        return OperateResult.CreateSuccessResult();
    }


    /**
     * 批量读取bool类型数据，支持的类型为X,Y,S,T,C，具体的地址范围取决于PLC的类型，地址支持动态指定站号，例如：s=2;D100<br />
     * Read bool data in batches. The supported types are X, Y, S, T, C. The specific address range depends on the type of PLC,
     * The address supports dynamically specifying the station number, for example: s=2;D100
     * @param plc PLC通信对象
     * @param address 地址信息，比如X10,Y17，注意X，Y的地址是8进制的
     * @param length 读取的长度
     * @return 读取结果信息
     */
    public static OperateResultExOne<boolean[]> ReadBool( MelsecFxLinksOverTcp plc, String address, short length ) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
            address = extraPara.Content2;
        }

        // 解析指令
        OperateResultExOne<ArrayList<byte[]>> command = BuildReadCommand(stat, address, length, true, plc.getWaittingTime());
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Boolean> result = new ArrayList<>();
        for (int i = 0; i < command.Content.size(); i++) {
            // 核心交互
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 结果验证
            OperateResultExOne<byte[]> extra = CheckPlcResponse(read.Content);
            if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult(extra);

            // 提取结果
            Utilities.ArrayListAddArray(result, Utilities.getBoolArray(extra.Content, (byte) 0x30));
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(result));
    }

    /**
     * 批量写入bool类型的数组，支持的类型为X,Y,S,T,C，具体的地址范围取决于PLC的类型，地址支持动态指定站号，例如：s=2;D100<br />
     * Write arrays of type bool in batches. The supported types are X, Y, S, T, C. The specific address range depends on the type of PLC,
     * The address supports dynamically specifying the station number, for example: s=2;D100
     * @param plc PLC通信对象
     * @param address PLC的地址信息
     * @param value 数据信息
     * @return 是否写入成功
     */
    public static OperateResult Write( MelsecFxLinksOverTcp plc, String address, boolean[] value ) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
            address = extraPara.Content2;
        }

        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteBoolCommand(stat, address, value, plc.getWaittingTime());
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        OperateResultExOne<byte[]> extra = CheckPlcResponse(read.Content);
        if (!extra.IsSuccess) return extra;

        // 提取结果
        return OperateResult.CreateSuccessResult();
    }

    /**
     * <b>[商业授权]</b> 启动PLC的操作，可以携带额外的参数信息，指定站号。举例：s=2; 注意：分号是必须的。<br />
     * <b>[Authorization]</b> Start the PLC operation, you can carry additional parameter information and specify the station number. Example: s=2; Note: The semicolon is required.
     * @param plc PLC通信对象
     * @param parameter 允许携带的参数信息，例如s=2; 也可以为空
     * @return 是否启动成功
     */
    public static OperateResult StartPLC( MelsecFxLinksOverTcp plc, String parameter ) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(parameter, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
        }

        // 解析指令
        OperateResultExOne<byte[]> command = BuildStart(stat, plc.getWaittingTime());
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        OperateResultExOne<byte[]> extra = CheckPlcResponse(read.Content);
        if (!extra.IsSuccess) return extra;

        // 提取结果
        return OperateResult.CreateSuccessResult();
    }

    /**
     * <b>[商业授权]</b> 停止PLC的操作，可以携带额外的参数信息，指定站号。举例：s=2; 注意：分号是必须的。<br />
     * <b>[Authorization]</b> Stop PLC operation, you can carry additional parameter information and specify the station number. Example: s=2; Note: The semicolon is required.
     * @param plc PLC通信对象
     * @param parameter 允许携带的参数信息，例如s=2; 也可以为空
     * @return 是否停止成功
     */
    public static OperateResult StopPLC( MelsecFxLinksOverTcp plc, String parameter ) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(parameter, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
        }

        // 解析指令
        OperateResultExOne<byte[]> command = BuildStop(stat, plc.getWaittingTime());
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        OperateResultExOne<byte[]> extra = CheckPlcResponse(read.Content);
        if (!extra.IsSuccess) return extra;

        // 提取结果
        return OperateResult.CreateSuccessResult();
    }

    /**
     * <b>[商业授权]</b> 读取PLC的型号信息，可以携带额外的参数信息，指定站号。举例：s=2; 注意：分号是必须的。<br />
     * <b>[Authorization]</b> Read the PLC model information, you can carry additional parameter information, and specify the station number. Example: s=2; Note: The semicolon is required.
     * @param plc PLC通信对象
     * @param parameter 允许携带的参数信息，例如s=2; 也可以为空
     * @return 带PLC型号的结果信息
     */
    public static OperateResultExOne<String> ReadPlcType( MelsecFxLinksOverTcp plc, String parameter ) {
        byte stat = plc.getStation();
        OperateResultExTwo<Integer, String> extraPara = PlcHelper.ExtractParameter(parameter, "s");
        if (extraPara.IsSuccess) {
            stat = extraPara.Content1.byteValue();
        }

        // 解析指令
        OperateResultExOne<byte[]> command = BuildReadPlcType(stat, plc.getWaittingTime());
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心交互
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 结果验证
        OperateResultExOne<byte[]> extra = CheckPlcResponse(read.Content);
        if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult(extra);

        // 提取结果
        return GetPlcTypeFromCode(new String(read.Content, 5, 2, StandardCharsets.US_ASCII));
    }
}
