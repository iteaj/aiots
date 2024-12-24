package com.iteaj.plugin.protocol.plc.core.profinet.Keyence;


import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

/**
 * KeyenceNano的基本辅助方法
 */
public class KeyenceNanoHelper {
    /**
     * 连接PLC的命令报文<br />
     * Command message to connect to PLC
     * @param station 当前PLC的站号信息
     * @param useStation 是否启动站号命令
     * @return 连接的报文命令
     */
    public static byte[] GetConnectCmd(byte station, boolean useStation) {
        return useStation ? String.format("CR %02d\r", station).getBytes(StandardCharsets.US_ASCII) : "CR\r".getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * 断开PLC连接的命令报文<br />
     * Command message to disconnect PLC
     * @param station 当前PLC的站号信息
     * @param useStation 是否启动站号命令
     * @return 断开的报文命令
     */
    public static byte[] GetDisConnectCmd(byte station, boolean useStation) {
        return "CQ\r".getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * 获取当前的地址类型是字数据的倍数关系
     * @param type 地址的类型
     * @return 倍数关系
     */
    public static int GetWordAddressMultiple(String type) {
        if (type.equals("CTH") || type.equals("CTC") || type.equals("C") || type.equals("T") || type.equals("TS") || type.equals("TC") || type.equals("CS") || type.equals("CC") || type.equals("AT"))
            return 2;
        else if (type.equals("DM") || type.equals("CM") || type.equals("TM") || type.equals("EM") || type.equals("FM") || type.equals("Z") || type.equals("W") || type.equals("ZF") || type.equals("VM"))
            return 1;
        return 1;
    }

    /**
     * 建立读取PLC数据的指令，需要传入地址数据，以及读取的长度，地址示例参照类的说明文档<br />
     * To create a command to read PLC data, you need to pass in the address data, and the length of the read. For an example of the address, refer to the class documentation
     * @param address 软元件地址
     * @param length 读取长度
     * @return 是否建立成功
     */
    public static OperateResultExOne<byte[]> BuildReadCommand(String address, short length) {
        OperateResultExTwo<String, Integer> addressResult = KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        if (length > 1) length = (short) (length / GetWordAddressMultiple(addressResult.Content1));

        StringBuilder cmd = new StringBuilder();
        cmd.append("RDS");                               // 批量读取
        cmd.append(" ");                                 // 空格符
        cmd.append(addressResult.Content1);              // 软元件类型，如DM
        cmd.append(addressResult.Content2.toString());  // 软元件的地址，如1000
        cmd.append(" ");                                 // 空格符
        cmd.append(String.valueOf(length));
        cmd.append("\r");                                //结束符

        byte[] _PLCCommand = cmd.toString().getBytes(StandardCharsets.US_ASCII);
        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 建立写入PLC数据的指令，需要传入地址数据，以及写入的数据信息，地址示例参照类的说明文档<br />
     * To create a command to write PLC data, you need to pass in the address data and the written data information. For an example of the address, refer to the class documentation
     * @param address 软元件地址
     * @param value 转换后的数据
     * @return 是否成功的信息
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, byte[] value) {
        OperateResultExTwo<String, Integer> addressResult = KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        StringBuilder cmd = new StringBuilder();
        cmd.append("WRS");                         // 批量读取
        cmd.append(" ");                           // 空格符
        cmd.append(addressResult.Content1);        // 软元件地址
        cmd.append(addressResult.Content2);        // 软元件地址
        cmd.append(" ");                           // 空格符
        int length = value.length / (GetWordAddressMultiple(addressResult.Content1) * 2);
        cmd.append(length);
        for (int i = 0; i < length; i++) {
            cmd.append(" ");
            cmd.append(Utilities.getUShort(value, i * GetWordAddressMultiple(addressResult.Content1) * 2));
        }
        cmd.append("\r");

        return OperateResultExOne.CreateSuccessResult(cmd.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 构建写入扩展单元缓冲寄存器的报文命令，需要传入单元编号，地址，写入的数据，实际写入的数据格式才有无符号的方式<br />
     * To construct a message command to write to the buffer register of the expansion unit, the unit number, address,
     * and data to be written need to be passed in, and the format of the actually written data is unsigned.
     * @param unit 单元编号0~48
     * @param address 地址0~32767
     * @param value 写入的数据信息，单次交互最大256个字
     * @return 包含是否成功的报文对象
     */
    public static OperateResultExOne<byte[]> BuildWriteExpansionMemoryCommand(byte unit, int address, byte[] value) {
        StringBuilder cmd = new StringBuilder();
        cmd.append("UWR");                         // 批量读取
        cmd.append(" ");                           // 空格符
        cmd.append(unit);                          // 单元编号
        cmd.append(" ");                           // 空格符
        cmd.append(address);                       // 地址
        cmd.append(".U");                          // 数据格式
        cmd.append(" ");                           // 空格符
        int length = value.length / 2;
        cmd.append(length);
        for (int i = 0; i < length; i++) {
            cmd.append(" ");
            cmd.append(Utilities.getUShort(value, i * 2));
        }
        cmd.append("\r");

        return OperateResultExOne.CreateSuccessResult(cmd.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 建立写入bool数据的指令，针对地址类型为 R,CR,MR,LR<br />
     * Create instructions to write bool data, address type is R, CR, MR, LR+
     * @param address 软元件地址
     * @param value 转换后的数据
     * @return 是否成功的信息
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, boolean value) {
        OperateResultExTwo<String, Integer> addressResult = KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        StringBuilder cmd = new StringBuilder();
        if (value)
            cmd.append("ST");                      // 置位
        else
            cmd.append("RS");                      // 复位
        cmd.append(" ");                           // 空格符
        cmd.append(addressResult.Content1);        // 软元件地址
        cmd.append(addressResult.Content2);        // 软元件地址
        cmd.append("\r");                          // 空格符
        return OperateResultExOne.CreateSuccessResult(cmd.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 批量写入数据位到plc地址，针对地址格式为 R,B,CR,MR,LR,VB<br />
     * Write data bits in batches to the plc address, and the address format is R, B, CR, MR, LR, VB
     * @param address PLC的地址
     * @param value 等待写入的bool数组
     * @return 写入bool数组的命令报文
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, boolean[] value) {
        OperateResultExTwo<String, Integer> addressResult = KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        StringBuilder cmd = new StringBuilder();
        cmd.append("WRS");
        cmd.append(" ");                           // 空格符
        cmd.append(addressResult.Content1);        // 软元件地址
        cmd.append(addressResult.Content2);        // 软元件地址
        cmd.append(" ");                           // 空格符
        cmd.append(value.length);      // 写入的数据长度
        for (int i = 0; i < value.length; i++) {
            cmd.append(" ");                           // 空格符
            cmd.append(value[i] ? "1" : "0");
        }
        cmd.append("\r");                          // 空格符
        return OperateResultExOne.CreateSuccessResult(cmd.toString().getBytes(StandardCharsets.US_ASCII));
    }

    private static String GetErrorText(String err) {
        if (err.startsWith("E0")) return StringResources.Language.KeyenceNanoE0();
        if (err.startsWith("E1")) return StringResources.Language.KeyenceNanoE1();
        if (err.startsWith("E2")) return StringResources.Language.KeyenceNanoE2();
        if (err.startsWith("E4")) return StringResources.Language.KeyenceNanoE4();
        if (err.startsWith("E5")) return StringResources.Language.KeyenceNanoE5();
        if (err.startsWith("E6")) return StringResources.Language.KeyenceNanoE6();
        return StringResources.Language.UnknownError() + " " + err;
    }

    /**
     * 校验读取返回数据状态，主要返回的第一个字节是不是E<br />
     * Check the status of the data returned from reading, whether the first byte returned is E
     * @param ack 反馈信息
     * @return 是否成功的信息
     */
    public static OperateResult CheckPlcReadResponse(byte[] ack) {
        if (ack.length == 0) return new OperateResult(StringResources.Language.MelsecFxReceiveZero());
        if (ack[0] == 0x45) return new OperateResult(GetErrorText(new String(ack, StandardCharsets.US_ASCII)));
        if ((ack[ack.length - 1] != AsciiControl.LF) && (ack[ack.length - 2] != AsciiControl.CR))
            return new OperateResult(StringResources.Language.MelsecFxAckWrong() + " Actual: " + SoftBasic.ByteToHexString(ack, ' '));
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 校验写入返回数据状态，检测返回的数据是不是OK<br />
     * Verify the status of the returned data written and check whether the returned data is OK
     * @param ack 反馈信息
     * @return 是否成功的信息
     */
    public static OperateResult CheckPlcWriteResponse(byte[] ack) {
        if (ack.length == 0) return new OperateResult(StringResources.Language.MelsecFxReceiveZero());
        if (ack[0] == 0x4F && ack[1] == 0x4B) return OperateResult.CreateSuccessResult();
        return new OperateResult(GetErrorText(new String(ack, StandardCharsets.US_ASCII)));
    }

    /**
     * 从PLC反馈的数据进行提炼Bool操作<br />
     * Refine Bool operation from data fed back from PLC
     * @param addressType 地址的数据类型
     * @param response PLC反馈的真实数据
     * @return 数据提炼后的真实数据
     */
    public static OperateResultExOne<boolean[]> ExtractActualBoolData(String addressType, byte[] response) {
        try {
            if (Utilities.IsStringNullOrEmpty(addressType)) addressType = "R";

            String strResponse = new String(SoftBasic.BytesArrayRemoveLast(response, 2), StandardCharsets.US_ASCII);
            if (addressType.equals("R") || addressType.equals("CR") || addressType.equals("MR") || addressType.equals("LR") || addressType.equals("B") || addressType.equals("VB")) {
                String[] splits = strResponse.split("\\s+");
                boolean[] values = new boolean[splits.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = splits[i].equals("1");
                }
                return OperateResultExOne.CreateSuccessResult(values);
            } else if (addressType.equals("T") || addressType.equals("C") || addressType.equals("CTH") || addressType.equals("CTC")) {
                String[] splits = strResponse.split("\\s+");
                boolean[] values = new boolean[splits.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = splits[i].startsWith("1");
                }
                return OperateResultExOne.CreateSuccessResult(values);
            } else {
                return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
            }
        } catch (Exception ex) {
            return new OperateResultExOne<boolean[]>("Extract Msg：" + ex.getMessage() +
                    "\r\nData: " + SoftBasic.ByteToHexString(response));
        }
    }

    /**
     * 从PLC反馈的数据进行提炼操作<br />
     * Refining operation from data fed back from PLC
     * @param addressType 地址的数据类型
     * @param response PLC反馈的真实数据
     * @return 数据提炼后的真实数据
     */
    public static OperateResultExOne<byte[]> ExtractActualData(String addressType, byte[] response) {
        try {
            if (Utilities.IsStringNullOrEmpty(addressType)) addressType = "R";

            String strResponse = new String(SoftBasic.BytesArrayRemoveLast(response, 2), StandardCharsets.US_ASCII);
            String[] splits = strResponse.split("\\s+");
            if (addressType.equals("DM") || addressType.equals("EM") || addressType.equals("FM") || addressType.equals("ZF") || addressType.equals("W") ||
                    addressType.equals("TM") || addressType.equals("Z") || addressType.equals("CM") || addressType.equals("VM")) {
                byte[] buffer = new byte[splits.length * 2];
                for (int i = 0; i < splits.length; i++) {
                    Utilities.ByteArrayCopyTo(Utilities.getBytes((short) Integer.parseInt(splits[i])), buffer, i * 2);
                }
                return OperateResultExOne.CreateSuccessResult(buffer);
            } else if (addressType.equals("AT") || addressType.equals("TC") || addressType.equals("CC") || addressType.equals("TS") || addressType.equals("CS")) {
                byte[] buffer = new byte[splits.length * 4];
                for (int i = 0; i < splits.length; i++) {
                    Utilities.ByteArrayCopyTo(Utilities.getBytes(Integer.parseInt(splits[i])), buffer, i * 4);
                }
                return OperateResultExOne.CreateSuccessResult(buffer);
            } else if (addressType.equals("T") || addressType.equals("C") || addressType.equals("CTH") || addressType.equals("CTC")) {
                byte[] buffer = new byte[splits.length * 4];
                for (int i = 0; i < splits.length; i++) {
                    String[] datas = splits[i].split(",");
                    Utilities.ByteArrayCopyTo(Utilities.getBytes(Integer.parseInt(datas[1])), buffer, i * 4);
                }
                return OperateResultExOne.CreateSuccessResult(buffer);
            } else {
                return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
            }
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Extract Msg：" + ex.getMessage() +
                    "\r\nData: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    /**
     * 解析数据地址成不同的Keyence地址类型<br />
     * Parse data addresses into different keyence address types
     * @param address 数据地址
     * @return 地址结果对象
     */
    public static OperateResultExTwo<String, Integer> KvAnalysisAddress(String address) {
        try {
            if (address.startsWith("CTH") || address.startsWith("cth"))
                return OperateResultExTwo.CreateSuccessResult("CTH", Integer.parseInt(address.substring(3)));
            else if (address.startsWith("CTC") || address.startsWith("ctc"))
                return OperateResultExTwo.CreateSuccessResult("CTC", Integer.parseInt(address.substring(3)));
            else if (address.startsWith("CR") || address.startsWith("cr"))
                return OperateResultExTwo.CreateSuccessResult("CR", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("MR") || address.startsWith("mr"))
                return OperateResultExTwo.CreateSuccessResult("MR", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("LR") || address.startsWith("lr"))
                return OperateResultExTwo.CreateSuccessResult("LR", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("DM") || address.startsWith("dm"))
                return OperateResultExTwo.CreateSuccessResult("DM", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("CM") || address.startsWith("cm"))
                return OperateResultExTwo.CreateSuccessResult("CM", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("W") || address.startsWith("w"))
                return OperateResultExTwo.CreateSuccessResult("W", Integer.parseInt(address.substring(1)));
            else if (address.startsWith("TM") || address.startsWith("tm"))
                return OperateResultExTwo.CreateSuccessResult("TM", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("VM") || address.startsWith("vm"))
                return OperateResultExTwo.CreateSuccessResult("VM", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("EM") || address.startsWith("em"))
                return OperateResultExTwo.CreateSuccessResult("EM", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("FM") || address.startsWith("fm"))
                return OperateResultExTwo.CreateSuccessResult("EM", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("ZF") || address.startsWith("zf"))
                return OperateResultExTwo.CreateSuccessResult("ZF", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("AT") || address.startsWith("at"))
                return OperateResultExTwo.CreateSuccessResult("AT", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("TS") || address.startsWith("ts"))
                return OperateResultExTwo.CreateSuccessResult("TS", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("TC") || address.startsWith("tc"))
                return OperateResultExTwo.CreateSuccessResult("TC", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("CC") || address.startsWith("cc"))
                return OperateResultExTwo.CreateSuccessResult("CC", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("CS") || address.startsWith("cs"))
                return OperateResultExTwo.CreateSuccessResult("CS", Integer.parseInt(address.substring(2)));
            else if (address.startsWith("Z") || address.startsWith("z"))
                return OperateResultExTwo.CreateSuccessResult("Z", Integer.parseInt(address.substring(1)));
            else if (address.startsWith("R") || address.startsWith("r"))
                return OperateResultExTwo.CreateSuccessResult("", Integer.parseInt(address.substring(1)));
            else if (address.startsWith("B") || address.startsWith("b"))
                return OperateResultExTwo.CreateSuccessResult("B", Integer.parseInt(address.substring(1)));
            else if (address.startsWith("T") || address.startsWith("t"))
                return OperateResultExTwo.CreateSuccessResult("T", Integer.parseInt(address.substring(1)));
            else if (address.startsWith("C") || address.startsWith("c"))
                return OperateResultExTwo.CreateSuccessResult("C", Integer.parseInt(address.substring(1)));
            else
                throw new Exception(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExTwo<>(ex.getMessage());
        }
    }


    public static OperateResultExOne<byte[]> Read(IReadWriteDevice keyence, String address, short length) {
        if (address.startsWith("unit=")) {
            OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "unit", 0);
            address = extra.Content2;
            byte unit = extra.Content1.byteValue();
            try {
                short offset = Short.parseShort(address);
                return ReadExpansionMemory(keyence, unit, offset, length);
            } catch (Exception ex) {
                return new OperateResultExOne<byte[]>("Address is not right, convert ushort wrong! " + ex.getMessage());
            }

        }

        // 获取指令
        OperateResultExOne<byte[]> command = BuildReadCommand(address, length);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心交互
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 反馈检查
        OperateResult ackResult = CheckPlcReadResponse(read.Content);
        if (!ackResult.IsSuccess) return OperateResultExOne.CreateFailedResult(ackResult);

        OperateResultExTwo<String, Integer> addressResult = KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        // 数据提炼
        return ExtractActualData(addressResult.Content1, read.Content);
    }

    public static OperateResult Write(IReadWriteDevice keyence, String address, byte[] value) {
        // 获取写入
        OperateResultExOne<byte[]> command = BuildWriteCommand(address, value);
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        OperateResult checkResult = CheckPlcWriteResponse(read.Content);
        if (!checkResult.IsSuccess) return checkResult;

        return OperateResult.CreateSuccessResult();
    }

    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteDevice keyence, String address, short length) {
        // 获取指令
        OperateResultExOne<byte[]> command = BuildReadCommand(address, length);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心交互
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 反馈检查
        OperateResult ackResult = CheckPlcReadResponse(read.Content);
        if (!ackResult.IsSuccess) return OperateResultExOne.CreateFailedResult(ackResult);

        OperateResultExTwo<String, Integer> addressResult = KvAnalysisAddress(address);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        // 数据提炼
        return ExtractActualBoolData(addressResult.Content1, read.Content);
    }

    public static OperateResult Write(IReadWriteDevice keyence, String address, boolean value) {
        // 获取写入
        OperateResultExOne<byte[]> command = BuildWriteCommand(address, value);
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        OperateResult checkResult = CheckPlcWriteResponse(read.Content);
        if (!checkResult.IsSuccess) return checkResult;

        return OperateResult.CreateSuccessResult();
    }

    public static OperateResult Write(IReadWriteDevice keyence, String address, boolean[] value) {
        // 获取写入
        OperateResultExOne<byte[]> command = BuildWriteCommand(address, value);
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        OperateResult checkResult = CheckPlcWriteResponse(read.Content);
        if (!checkResult.IsSuccess) return checkResult;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * <c>[商业授权]</c> 查询PLC的型号信息<br />
     * <b>[Authorization]</b> Query PLC model information
     * @param keyence PLC通信对象
     * @return 包含型号的结果对象
     */
    public static OperateResultExOne<KeyencePLCS> ReadPlcType(IReadWriteDevice keyence) {

        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer("?K\r".getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResult check = CheckPlcReadResponse(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        String type = new String(SoftBasic.BytesArrayRemoveLast(read.Content, 2), StandardCharsets.US_ASCII);
        switch (type) {
            case "48":
            case "49":
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV700);
            case "50":
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV1000);
            case "51":
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV3000);
            case "52":
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV5000);
            case "53":
                return OperateResultExOne.CreateSuccessResult(KeyencePLCS.KV5500);
            default:
                return new OperateResultExOne<KeyencePLCS>("Unknow type:" + type);
        }
    }

    /**
     * <c>[商业授权]</c> 读取当前PLC的模式，如果是0，代表 PROG模式或者梯形图未登录，如果为1，代表RUN模式<br />
     *  <b>[Authorization]</b> Read the current PLC mode, if it is 0, it means PROG mode or the ladder diagram is not registered, if it is 1, it means RUN mode
     * @param keyence PLC通信对象
     * @return 包含模式的结果对象
     */
    public static OperateResultExOne<Integer> ReadPlcMode(IReadWriteDevice keyence) {

        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer("?M\r".getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResult check = CheckPlcReadResponse(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        String type = new String(SoftBasic.BytesArrayRemoveLast(read.Content, 2), StandardCharsets.US_ASCII);
        if (type.equals("0")) return OperateResultExOne.CreateSuccessResult(0);
        return OperateResultExOne.CreateSuccessResult(1);
    }

    /**
     * <c>[商业授权]</c> 设置PLC的时间<br />
     * <b>[Authorization]</b> Set PLC time
     * @param keyence PLC通信对象
     * @param dateTime 时间数据
     * @return 是否设置成功
     */
    public static OperateResult SetPlcDateTime(IReadWriteDevice keyence, Date dateTime) {


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);

        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer((String.format("WRT %02d %02d %02d %02d %02d %02d %d",
                calendar.get(Calendar.YEAR) - 2000,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.DAY_OF_WEEK))).getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) return read;

        return CheckPlcWriteResponse(read.Content);
    }

    /**
     * <c>[商业授权]</c> 读取指定软元件的注释信息<br />
     * <b>[Authorization]</b> Read the comment information of the specified device
     * @param keyence PLC通信对象
     * @param address 软元件的地址
     * @return 软元件的注释信息
     */
    public static OperateResultExOne<String> ReadAddressAnnotation(IReadWriteDevice keyence, String address) {

        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(("RDC " + address + "\r").getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResult check = CheckPlcReadResponse(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        return OperateResultExOne.CreateSuccessResult(new String(SoftBasic.BytesArrayRemoveLast(read.Content, 2)).trim());
    }

    /**
     * <c>[商业授权]</c> 从扩展单元缓冲存储器连续读取指定个数的数据，单位为字<br />
     * <b>[Authorization]</b> Continuously read the specified number of data from the expansion unit buffer memory, the unit is word
     * @param keyence PLC的通信对象
     * @param unit 单元编号
     * @param address 偏移地址
     * @param length 读取的长度，单位为字
     * @return 包含是否成功的原始字节数组
     */
    public static OperateResultExOne<byte[]> ReadExpansionMemory(IReadWriteDevice keyence, byte unit, short address, short length) {

        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(("URD " + unit + " " + address + ".U " + length + "\r").getBytes(StandardCharsets.US_ASCII));
        if (!read.IsSuccess) return read;

        OperateResult check = CheckPlcReadResponse(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        return ExtractActualData("DM", read.Content); // 按照DM来解析，因为上面读取的命令是.U格式的，而DM默认的也是.U
    }

    /**
     * <c>[商业授权]</c> 将原始字节数据写入到扩展的缓冲存储器，需要指定单元编号，偏移地址，写入的数据<br />
     * <b>[Authorization]</b> To write the original byte data to the extended buffer memory, you need to specify the unit number, offset address, and write data
     * @param keyence PLC通信对象信息
     * @param unit 单元编号
     * @param address 偏移地址
     * @param value 等待写入的原始字节数据
     * @return 是否写入成功的结果对象
     */
    public static OperateResult WriteExpansionMemory(IReadWriteDevice keyence, byte unit, short address, byte[] value) {


        OperateResultExOne<byte[]> read = keyence.ReadFromCoreServer(BuildWriteExpansionMemoryCommand(unit, address, value).Content);
        if (!read.IsSuccess) return read;

        return CheckPlcWriteResponse(read.Content);
    }
}
