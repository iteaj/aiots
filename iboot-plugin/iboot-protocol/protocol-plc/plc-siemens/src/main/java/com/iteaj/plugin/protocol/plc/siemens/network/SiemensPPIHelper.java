package com.iteaj.plugin.protocol.plc.siemens.network;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

public class SiemensPPIHelper {

    /**
     * 检查接收的数据是否完整
     * @param ms 内存流数据
     * @return 是否完整
     */
    public static boolean CheckReceiveDataComplete( MemoryStream ms )
    {
        byte[] buffer = ms.ToArray( );
        if (buffer.length == 1 && buffer[0] == (byte) 0xE5) return true;

        if (buffer.length > 6 && buffer[0] == 0x68 && (buffer[1] + 6 == buffer.length) && buffer[buffer.length - 1] == AsciiControl.SYN) return true;
        return false;
    }

    /**
     * 解析数据地址，解析出地址类型，起始地址，DB块的地址<br />
     * Parse data address, parse out address type, start address, db block address
     * @param address 起始地址，例如M100，I0，Q0，V100 -> Start address, such as M100,I0,Q0,V100
     * @return 解析数据地址，解析出地址类型，起始地址，DB块的地址 -> arse data address, parse out address type, start address, db block address
     */
    public static OperateResultExThree<Byte, Integer, Integer> AnalysisAddress(String address ) {
        OperateResultExThree<Byte, Integer, Integer> result = new OperateResultExThree<>();
        try {
            result.Content3 = 0;
            if (address.substring(0, 2).equals("AI")) {
                result.Content1 = 0x06;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(2), false);
            } else if (address.substring(0, 2).equals("AQ")) {
                result.Content1 = 0x07;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(2), false);
            } else if (address.charAt(0) == 'T') {
                result.Content1 = 0x1F;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
            } else if (address.charAt(0) == 'C') {
                result.Content1 = 0x1E;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
            } else if (address.substring(0, 2).equals("SM")) {
                result.Content1 = 0x05;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(2), false);
            } else if (address.charAt(0) == 'S') {
                result.Content1 = 0x04;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
            } else if (address.charAt(0) == 'I') {
                result.Content1 = (byte) 0x81;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
            } else if (address.charAt(0) == 'Q') {
                result.Content1 = (byte) 0x82;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
            } else if (address.charAt(0) == 'M') {
                result.Content1 = (byte) 0x83;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
            } else if (address.charAt(0) == 'D' || address.substring(0, 2).equals("DB")) {
                result.Content1 = (byte) 0x84;
                String[] adds = address.split("\\.");
                if (address.charAt(1) == 'B') {
                    result.Content3 = Integer.parseInt(adds[0].substring(2));
                } else {
                    result.Content3 = Integer.parseInt(adds[0].substring(1));
                }

                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(address.indexOf(".") + 1), false);
            } else if (address.charAt(0) == 'V') {
                result.Content1 = (byte) 0x84;
                result.Content3 = 1;
                result.Content2 = S7AddressData.CalculateAddressStarted(address.substring(1), false);
            } else {
                result.Message = StringResources.Language.NotSupportedDataType();
                result.Content1 = 0;
                result.Content2 = 0;
                result.Content3 = 0;
                return result;
            }
        } catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }

        result.IsSuccess = true;
        return result;
    }

    /**
     * 生成一个读取字数据指令头的通用方法<br />
     * A general method for generating a command header to read a Word data
     * @param station 设备的站号信息 -> Station number information for the device
     * @param address 起始地址，例如M100，I0，Q0，V100 -> Start address, such as M100,I0,Q0,V100</param>
     * @param length 读取数据长度 -> Read Data length
     * @param isBit 是否为位读取
     * @return 包含结果对象的报文 -> Message containing the result object
     */
    public static OperateResultExOne<byte[]> BuildReadCommand(byte station, String address, short length, boolean isBit ) {
        OperateResultExThree<Byte, Integer, Integer> analysis = AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] _PLCCommand = new byte[33];
        _PLCCommand[0] = 0x68;
        _PLCCommand[1] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[2] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[3] = 0x68;
        _PLCCommand[4] = station;
        _PLCCommand[5] = 0x00;
        _PLCCommand[6] = 0x6C;
        _PLCCommand[7] = 0x32;
        _PLCCommand[8] = 0x01;
        _PLCCommand[9] = 0x00;
        _PLCCommand[10] = 0x00;
        _PLCCommand[11] = 0x00;
        _PLCCommand[12] = 0x00;
        _PLCCommand[13] = 0x00;
        _PLCCommand[14] = 0x0E;
        _PLCCommand[15] = 0x00;
        _PLCCommand[16] = 0x00;
        _PLCCommand[17] = 0x04;
        _PLCCommand[18] = 0x01;
        _PLCCommand[19] = 0x12;
        _PLCCommand[20] = 0x0A;
        _PLCCommand[21] = 0x10;

        _PLCCommand[22] = isBit ? (byte) 0x01 : (byte) 0x02;
        _PLCCommand[23] = 0x00;
        _PLCCommand[24] = Utilities.getBytes(length)[0];
        _PLCCommand[25] = Utilities.getBytes(length)[1];
        _PLCCommand[26] = analysis.Content3.byteValue();
        _PLCCommand[27] = analysis.Content1;
        _PLCCommand[28] = Utilities.getBytes(analysis.Content2)[2];
        _PLCCommand[29] = Utilities.getBytes(analysis.Content2)[1];
        _PLCCommand[30] = Utilities.getBytes(analysis.Content2)[0];

        int count = 0;
        for (int i = 4; i < 31; i++) {
            count += _PLCCommand[i];
        }
        _PLCCommand[31] = Utilities.getBytes(count)[0];
        _PLCCommand[32] = 0x16;

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 生成一个写入PLC数据信息的报文内容
     * @param station PLC的站号
     * @param address 地址
     * @param values 数据值
     * @return 是否写入成功
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( byte station, String address, byte[] values ) {
        OperateResultExThree<Byte, Integer, Integer> analysis = AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        int length = values.length;
        // 68 21 21 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10
        byte[] _PLCCommand = new byte[37 + values.length];
        _PLCCommand[0] = 0x68;
        _PLCCommand[1] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[2] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[3] = 0x68;
        _PLCCommand[4] = station;
        _PLCCommand[5] = 0x00;
        _PLCCommand[6] = 0x7C;
        _PLCCommand[7] = 0x32;
        _PLCCommand[8] = 0x01;
        _PLCCommand[9] = 0x00;
        _PLCCommand[10] = 0x00;
        _PLCCommand[11] = 0x00;
        _PLCCommand[12] = 0x00;
        _PLCCommand[13] = 0x00;
        _PLCCommand[14] = 0x0E;
        _PLCCommand[15] = 0x00;
        _PLCCommand[16] = (byte) (values.length + 4);
        _PLCCommand[17] = 0x05;
        _PLCCommand[18] = 0x01;
        _PLCCommand[19] = 0x12;
        _PLCCommand[20] = 0x0A;
        _PLCCommand[21] = 0x10;

        _PLCCommand[22] = 0x02;
        _PLCCommand[23] = 0x00;
        _PLCCommand[24] = Utilities.getBytes(length)[0];
        _PLCCommand[25] = Utilities.getBytes(length)[1];
        _PLCCommand[26] = analysis.Content3.byteValue();
        _PLCCommand[27] = analysis.Content1;
        _PLCCommand[28] = Utilities.getBytes(analysis.Content2)[2];
        _PLCCommand[29] = Utilities.getBytes(analysis.Content2)[1];
        _PLCCommand[30] = Utilities.getBytes(analysis.Content2)[0];

        _PLCCommand[31] = 0x00;
        _PLCCommand[32] = 0x04;
        _PLCCommand[33] = Utilities.getBytes(length * 8)[1];
        _PLCCommand[34] = Utilities.getBytes(length * 8)[0];

        System.arraycopy(values, 0, _PLCCommand, 35, values.length);

        int count = 0;
        for (int i = 4; i < _PLCCommand.length - 2; i++) {
            count += _PLCCommand[i];
        }
        _PLCCommand[_PLCCommand.length - 2] = Utilities.getBytes(count)[0];
        _PLCCommand[_PLCCommand.length - 1] = 0x16;


        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 根据错误代号信息，获取到指定的文本信息<br />
     * According to the error code information, get the specified text information
     * @param code 错误状态信息
     * @return 消息文本
     */
    public static String GetMsgFromStatus( byte code ) {
        switch (code) {
            case (byte) 0xFF:
                return "No error";
            case 0x01:
                return "Hardware fault";
            case 0x03:
                return "Illegal object access";
            case 0x05:
                return "Invalid address(incorrent variable address)";
            case 0x06:
                return "Data type is not supported";
            case 0x0A:
                return "Object does not exist or length error";
            default:
                return StringResources.Language.UnknownError();
        }
    }

    /**
     * 根据错误信息，获取到文本信息
     * @param errorClass 错误类型
     * @param errorCode 错误代码
     * @return 错误信息
     */
    public static String GetMsgFromStatus( byte errorClass, byte errorCode ) {
        if (errorClass == (byte) 0x80 && errorCode == 0x01) {
            return "Switch in wrong position for requested operation";
        } else if (errorClass == (byte) 0x81 && errorCode == 0x04) {
            return "Miscellaneous structure error in command.  Command is not supportedby CPU";
        } else if (errorClass == (byte) 0x84 && errorCode == 0x04) {
            return "CPU is busy processing an upload or download CPU cannot process command because of system fault condition";
        } else if (errorClass == (byte) 0x85 && errorCode == 0x00) {
            return "Length fields are not correct or do not agree with the amount of data received";
        } else if (errorClass == (byte) 0xD2) {
            return "Error in upload or download command";
        } else if (errorClass == (byte) 0xD6) {
            return "Protection error(password)";
        } else if (errorClass == (byte) 0xDC && errorCode == 0x01) {
            return "Error in time-of-day clock data";
        } else {
            return StringResources.Language.UnknownError();
        }
    }

    /**
     * 创建写入PLC的bool类型数据报文指令
     * @param station PLC的站号信息
     * @param address 地址信息
     * @param values bool[]数据值
     * @return 带有成功标识的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( byte station, String address, boolean[] values ) {
        OperateResultExThree<Byte, Integer, Integer> analysis = AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] bytesValue = SoftBasic.BoolArrayToByte(values);
        // 68 21 21 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10
        byte[] _PLCCommand = new byte[37 + bytesValue.length];
        _PLCCommand[0] = 0x68;
        _PLCCommand[1] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[2] = Utilities.getBytes(_PLCCommand.length - 6)[0];
        _PLCCommand[3] = 0x68;
        _PLCCommand[4] = station;
        _PLCCommand[5] = 0x00;
        _PLCCommand[6] = 0x7C;
        _PLCCommand[7] = 0x32;
        _PLCCommand[8] = 0x01;
        _PLCCommand[9] = 0x00;
        _PLCCommand[10] = 0x00;
        _PLCCommand[11] = 0x00;
        _PLCCommand[12] = 0x00;
        _PLCCommand[13] = 0x00;
        _PLCCommand[14] = 0x0E;
        _PLCCommand[15] = 0x00;
        _PLCCommand[16] = 0x05;
        _PLCCommand[17] = 0x05;
        _PLCCommand[18] = 0x01;
        _PLCCommand[19] = 0x12;
        _PLCCommand[20] = 0x0A;
        _PLCCommand[21] = 0x10;

        _PLCCommand[22] = 0x01;
        _PLCCommand[23] = 0x00;
        _PLCCommand[24] = Utilities.getBytes(values.length)[0];
        _PLCCommand[25] = Utilities.getBytes(values.length)[1];
        _PLCCommand[26] = analysis.Content3.byteValue();
        _PLCCommand[27] = analysis.Content1;
        _PLCCommand[28] = Utilities.getBytes(analysis.Content2)[2];
        _PLCCommand[29] = Utilities.getBytes(analysis.Content2)[1];
        _PLCCommand[30] = Utilities.getBytes(analysis.Content2)[0];

        _PLCCommand[31] = 0x00;
        _PLCCommand[32] = 0x03;
        _PLCCommand[33] = Utilities.getBytes(values.length)[1];
        _PLCCommand[34] = Utilities.getBytes(values.length)[0];

        System.arraycopy(bytesValue, 0, _PLCCommand, 35, bytesValue.length);

        int count = 0;
        for (int i = 4; i < _PLCCommand.length - 2; i++) {
            count += _PLCCommand[i];
        }
        _PLCCommand[_PLCCommand.length - 2] = Utilities.getBytes(count)[0];
        _PLCCommand[_PLCCommand.length - 1] = 0x16;


        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 检查西门子PLC的返回的数据和合法性，对反馈的数据进行初步的校验
     * @param content 服务器返回的原始的数据内容
     * @return 是否校验成功
     */
    public static OperateResult CheckResponse(byte[] content ) {
        if (content.length < 21)
            return new OperateResult(10000, "Failed, data too short:" + SoftBasic.ByteToHexString(content, ' '));
        if (content[17] != 0x00 || content[18] != 0x00)
            return new OperateResult(content[19], GetMsgFromStatus(content[18], content[19]));
        if (content[21] != (byte) 0xFF) return new OperateResult(content[21], GetMsgFromStatus(content[21]));
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 根据站号信息获取命令二次确认的报文信息
     * @param station 站号信息
     * @return 二次命令确认的报文
     */
    public static byte[] GetExecuteConfirm( byte station ) {
        byte[] buffer = new byte[]{0x10, 0x02, 0x00, 0x5C, 0x5E, 0x16};
        buffer[1] = station;

        int count = 0;
        for (int i = 1; i < 4; i++) {
            count += buffer[i];
        }
        buffer[4] = (byte) count;
        return buffer;
    }

    /**
     * 从西门子的PLC中读取数据信息，地址为"M100","AI100","I0","Q0","V100","S100"等<br />
     * Read data information from Siemens PLC with addresses "M100", "AI100", "I0", "Q0", "V100", "S100", etc.
     * @param plc PLC的通信对象
     * @param address 西门子的地址数据信息
     * @param length 数据长度
     * @param station 当前的站号信息
     * @param communicationLock 当前的同通信锁
     * @return 带返回结果的结果对象
     */
    public static OperateResultExOne<byte[]> Read(IReadWriteDevice plc, String address, short length, byte station, Object communicationLock ) {
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = extra.Content1.byteValue();
            address = extra.Content2;
        }

        // 解析指令
        OperateResultExOne<byte[]> command = BuildReadCommand(station, address, length, false);
        if (!command.IsSuccess) return command;

        synchronized (communicationLock) {
            // 第一次数据交互
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer(command.Content);
            if (!read1.IsSuccess) return read1;

            // 验证
            if (read1.Content[0] != (byte) 0xE5)
                return new OperateResultExOne<byte[]>("PLC Receive Check Failed:" + SoftBasic.ByteToHexString(read1.Content, ' '));

            // 第二次数据交互
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(GetExecuteConfirm(station));
            if (!read2.IsSuccess) return read2;

            // 错误码判断
            OperateResult check = CheckResponse(read2.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            // 数据提取
            byte[] buffer = new byte[length];
            if (read2.Content[21] == (byte) 0xFF && read2.Content[22] == 0x04) {
                System.arraycopy(read2.Content, 25, buffer, 0, length);
            }
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
    }

    /**
     * 从西门子的PLC中读取bool数据信息，地址为"M100.0","AI100.1","I0.3","Q0.6","V100.4","S100"等<br />
     * Read bool data information from Siemens PLC, the addresses are "M100.0", "AI100.1", "I0.3", "Q0.6", "V100.4", "S100", etc.
     * @param plc PLC的通信对象
     * @param address 西门子的地址数据信息
     * @param length 数据长度
     * @param station 当前的站号信息
     * @param communicationLock 当前的同通信锁
     * @return 带返回结果的结果对象
     */
    public static OperateResultExOne<boolean[]> ReadBool( IReadWriteDevice plc, String address, short length, byte station, Object communicationLock ) {
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = extra.Content1.byteValue();
            address = extra.Content2;
        }


        // 解析指令
        OperateResultExOne<byte[]> command = BuildReadCommand(station, address, length, true);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        synchronized (communicationLock) {
            // 第一次数据交互
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer(command.Content);
            if (!read1.IsSuccess) return OperateResultExOne.CreateFailedResult(read1);

            // 验证
            if (read1.Content[0] != (byte) 0xE5)
                return new OperateResultExOne<boolean[]>("PLC Receive Check Failed:" + SoftBasic.ByteToHexString(read1.Content, ' '));

            // 第二次数据交互
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(GetExecuteConfirm(station));
            if (!read2.IsSuccess) return OperateResultExOne.CreateFailedResult(read2);

            // 错误码判断
            OperateResult check = CheckResponse(read2.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            // 数据提取
            byte[] buffer = new byte[read2.Content.length - 27];
            if (read2.Content[21] == (byte) 0xFF && read2.Content[22] == 0x03) {
                System.arraycopy(read2.Content, 25, buffer, 0, buffer.length);
            }

            return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray(buffer, length));
        }
    }

    /**
     * 将字节数据写入到西门子PLC中，地址为"M100.0","AI100.1","I0.3","Q0.6","V100.4","S100"等<br />
     * Write byte data to Siemens PLC with addresses "M100.0", "AI100.1", "I0.3", "Q0.6", "V100.4", "S100", etc.
     * @param plc PLC的通信对象
     * @param address 西门子的地址数据信息
     * @param value 数据长度
     * @param station 当前的站号信息
     * @param communicationLock 当前的同通信锁
     * @return 带返回结果的结果对象
     */
    public static OperateResult Write( IReadWriteDevice plc, String address, byte[] value, byte station, Object communicationLock ) {
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = extra.Content1.byteValue();
            address = extra.Content2;
        }


        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteCommand(station, address, value);
        if (!command.IsSuccess) return command;

        synchronized (communicationLock) {
            // 第一次数据交互
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer(command.Content);
            if (!read1.IsSuccess) return read1;

            // 验证
            if (read1.Content[0] != (byte) 0xE5)
                return new OperateResultExOne<byte[]>("PLC Receive Check Failed:" + read1.Content[0]);

            // 第二次数据交互
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(GetExecuteConfirm(station));
            if (!read2.IsSuccess) return read2;

            // 错误码判断
            OperateResult check = CheckResponse(read2.Content);
            if (!check.IsSuccess) return check;

            // 数据提取
            return OperateResult.CreateSuccessResult();
        }
    }

    /**
     * 将bool数据写入到西门子PLC中，地址为"M100.0","AI100.1","I0.3","Q0.6","V100.4","S100"等<br />
     * Write the bool data to Siemens PLC with the addresses "M100.0", "AI100.1", "I0.3", "Q0.6", "V100.4", "S100", etc.
     * @param plc PLC的通信对象
     * @param address 西门子的地址数据信息
     * @param value 数据长度
     * @param station 当前的站号信息
     * @param communicationLock 当前的同通信锁
     * @return 带返回结果的结果对象
     */
    public static OperateResult Write( IReadWriteDevice plc, String address, boolean[] value, byte station, Object communicationLock ) {
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = extra.Content1.byteValue();
            address = extra.Content2;
        }


        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteCommand(station, address, value);
        if (!command.IsSuccess) return command;

        synchronized (communicationLock) {
            // 第一次数据交互
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer(command.Content);
            if (!read1.IsSuccess) return read1;

            // 验证
            if (read1.Content[0] != (byte) 0xE5)
                return new OperateResultExOne<byte[]>("PLC Receive Check Failed:" + read1.Content[0]);

            // 第二次数据交互
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(GetExecuteConfirm(station));
            if (!read2.IsSuccess) return read2;

            // 错误码判断
            OperateResult check = CheckResponse(read2.Content);
            if (!check.IsSuccess) return check;

            // 数据提取
            return OperateResult.CreateSuccessResult();
        }
    }

    /**
     * 启动西门子PLC为RUN模式，参数信息可以携带站号信息 "s=2;", 注意，分号是必须的。<br />
     * Start Siemens PLC in RUN mode, parameter information can carry station number information "s=2;", note that the semicolon is required.
     * @param plc PLC的通信对象
     * @param parameter >额外的参数信息，例如可以携带站号信息 "s=2;", 注意，分号是必须的。
     * @param station 当前的站号信息
     * @param communicationLock 当前的同通信锁
     * @return 是否启动成功
     */
    public static OperateResult Start( IReadWriteDevice plc, String parameter, byte station, Object communicationLock ) {
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(parameter, "s", station);
        if (extra.IsSuccess) {
            station = extra.Content1.byteValue();
        }
        byte[] cmd = new byte[]{0x68, 0x21, 0x21, 0x68, station, 0x00, 0x6C, 0x32, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x14, 0x00, 0x00, 0x28, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFD, 0x00, 0x00, 0x09, 0x50, 0x5F, 0x50, 0x52, 0x4F, 0x47, 0x52, 0x41, 0x4D, (byte) 0xAA, 0x16};

        synchronized (communicationLock) {
            // 第一次数据交互
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer(cmd);
            if (!read1.IsSuccess) return read1;

            // 验证
            if (read1.Content[0] != (byte) 0xE5)
                return new OperateResultExOne<byte[]>("PLC Receive Check Failed:" + read1.Content[0]);

            // 第二次数据交互
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(GetExecuteConfirm(station));
            if (!read2.IsSuccess) return read2;

            // 数据提取
            return OperateResult.CreateSuccessResult();
        }
    }

    /**
     * 停止西门子PLC，切换为Stop模式，参数信息可以携带站号信息 "s=2;", 注意，分号是必须的。<br />
     * Stop Siemens PLC and switch to Stop mode, parameter information can carry station number information "s=2;", note that the semicolon is required.
     * @param plc PLC的通信对象
     * @param parameter 额外的参数信息，例如可以携带站号信息 "s=2;", 注意，分号是必须的。
     * @param station 当前的站号信息
     * @param communicationLock 当前的同通信锁
     * @return 是否停止成功
     */
    public static OperateResult Stop(IReadWriteDevice plc, String parameter, byte station, Object communicationLock ) {
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(parameter, "s", station);
        if (extra.IsSuccess) {
            station = extra.Content1.byteValue();
        }


        byte[] cmd = new byte[]{0x68, 0x1D, 0x1D, 0x68, station, 0x00, 0x6C, 0x32, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x29, 0x00, 0x00, 0x00, 0x00, 0x00, 0x09, 0x50, 0x5F, 0x50, 0x52, 0x4F, 0x47, 0x52, 0x41, 0x4D, (byte) 0xAA, 0x16};

        synchronized (communicationLock) {
            // 第一次数据交互
            OperateResultExOne<byte[]> read1 = plc.ReadFromCoreServer(cmd);
            if (!read1.IsSuccess) return read1;

            // 验证
            if (read1.Content[0] != (byte) 0xE5)
                return new OperateResultExOne<byte[]>("PLC Receive Check Failed:" + read1.Content[0]);

            // 第二次数据交互
            OperateResultExOne<byte[]> read2 = plc.ReadFromCoreServer(GetExecuteConfirm(station));
            if (!read2.IsSuccess) return read2;

            // 数据提取
            return OperateResult.CreateSuccessResult();
        }
    }
}
