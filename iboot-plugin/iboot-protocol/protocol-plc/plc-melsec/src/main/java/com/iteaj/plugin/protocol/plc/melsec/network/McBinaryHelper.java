package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class McBinaryHelper {

    /**
     * 将MC协议的核心报文打包成一个可以直接对PLC进行发送的原始报文
     * @param mcCore MC协议的核心报文
     * @param networkNumber 网络号
     * @param networkStationNumber 网络站号
     * @return 原始报文信息
     */
    public static byte[] PackMcCommand(byte[] mcCore, byte networkNumber, byte networkStationNumber)
    {
        byte[] _PLCCommand = new byte[11 + mcCore.length];
        _PLCCommand[0] = 0x50;                                               // 副标题
        _PLCCommand[1] = 0x00;
        _PLCCommand[2] = networkNumber;                                      // 网络号
        _PLCCommand[3] = (byte) 0xFF;                                               // PLC编号
        _PLCCommand[4] = (byte) 0xFF;                                               // 目标模块IO编号
        _PLCCommand[5] = 0x03;
        _PLCCommand[6] = networkStationNumber;                               // 目标模块站号
        _PLCCommand[7] = (byte)((_PLCCommand.length - 9) % 256);             // 请求数据长度
        _PLCCommand[8] = (byte)((_PLCCommand.length - 9) / 256);
        _PLCCommand[9] = 0x0A;                                               // CPU监视定时器
        _PLCCommand[10] = 0x00;
        System.arraycopy(mcCore, 0, _PLCCommand, 11, mcCore.length);

        return _PLCCommand;
    }

    /**
     * 检查从MC返回的数据是否是合法的。
     * @param content 数据内容
     * @return 是否合法
     */
    public static OperateResult CheckResponseContentHelper(byte[] content )
    {
        int errorCode = Utilities.getUShort( content, 9 );
        if (errorCode != 0) return new OperateResult( errorCode, MelsecHelper.GetErrorDescription( errorCode ) );

        return OperateResult.CreateSuccessResult( );
    }


    /**
     * 从三菱地址，是否位读取进行创建读取的MC的核心报文<br />
     * From the Mitsubishi address, whether to read the core message of the MC for creating and reading
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param isBit       是否进行了位读取操作
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildReadMcCoreCommand(McAddressData addressData, boolean isBit) {
        byte[] command = new byte[10];
        command[0] = 0x01;                                                        // 批量读取数据命令
        command[1] = 0x04;
        command[2] = isBit ? (byte) 0x01 : (byte) 0x00;                             // 以点为单位还是字为单位成批读取
        command[3] = 0x00;
        command[4] = Utilities.getBytes(addressData.getAddressStart())[0];      // 起始地址的地位
        command[5] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[6] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[7] = (byte) addressData.getMcDataType().getDataCode();                   // 指明读取的数据
        command[8] = (byte) (addressData.getLength() % 256);                       // 软元件的长度
        command[9] = (byte) (addressData.getLength() / 256);

        return command;
    }

    /**
     * 以字为单位，创建数据写入的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param value       实际的原始数据信息
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildWriteWordCoreCommand(McAddressData addressData, byte[] value) {
        if (value == null) value = new byte[0];
        byte[] command = new byte[10 + value.length];
        command[0] = 0x01;                                                        // 批量写入数据命令
        command[1] = 0x14;
        command[2] = 0x00;                                                        // 以字为单位成批读取
        command[3] = 0x00;
        command[4] = Utilities.getBytes(addressData.getAddressStart())[0];        // 起始地址的地位
        command[5] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[6] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[7] = (byte) addressData.getMcDataType().getDataCode();                             // 指明写入的数据
        command[8] = (byte) (value.length / 2 % 256);                              // 软元件长度的地位
        command[9] = (byte) (value.length / 2 / 256);
        System.arraycopy(value, 0, command, 10, value.length);
        return command;
    }

    /**
     * 以位为单位，创建数据写入的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param value       原始的bool数组数据
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildWriteBitCoreCommand(McAddressData addressData, boolean[] value) {
        if (value == null) value = new boolean[0];
        byte[] buffer = MelsecHelper.TransBoolArrayToByteData(value);
        byte[] command = new byte[10 + buffer.length];
        command[0] = 0x01;                                                        // 批量写入数据命令
        command[1] = 0x14;
        command[2] = 0x01;                                                        // 以位为单位成批写入
        command[3] = 0x00;
        command[4] = Utilities.getBytes(addressData.getAddressStart())[0];        // 起始地址的地位
        command[5] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[6] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[7] = (byte) addressData.getMcDataType().getDataCode();                             // 指明写入的数据
        command[8] = (byte) (value.length % 256);                                  // 软元件长度的地位
        command[9] = (byte) (value.length / 256);
        System.arraycopy(buffer, 0, command, 10, buffer.length);

        return command;
    }
    /**
     * 从三菱扩展地址，是否位读取进行创建读取的MC的核心报文
     *
     * @param addressData 是否进行了位读取操作
     * @param extend      扩展指定
     * @param isBit       三菱Mc协议的数据地址
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildReadMcCoreExtendCommand(McAddressData addressData, short extend, boolean isBit) {
        byte[] command = new byte[17];
        command[0] = 0x01;                                                      // 批量读取数据命令
        command[1] = 0x04;
        command[2] = isBit ? (byte) 0x81 : (byte) 0x80;                           // 以点为单位还是字为单位成批读取
        command[3] = 0x00;
        command[4] = 0x00;
        command[5] = 0x00;
        command[6] = Utilities.getBytes(addressData.getAddressStart())[0];      // 起始地址的地位
        command[7] = Utilities.getBytes(addressData.getAddressStart())[1];
        command[8] = Utilities.getBytes(addressData.getAddressStart())[2];
        command[9] = (byte) addressData.getMcDataType().getDataCode();                           // 指明读取的数据
        command[10] = 0x00;
        command[11] = 0x00;
        command[12] = Utilities.getBytes(extend)[0];
        command[13] = Utilities.getBytes(extend)[1];
        command[14] = (byte) 0xF9;
        command[15] = (byte) (addressData.getLength() % 256);                          // 软元件的长度
        command[16] = (byte) (addressData.getLength() / 256);

        return command;
    }

    /**
     * 按字为单位随机读取的指令创建
     *
     * @param address 地址数组
     * @return 指令
     */
    public static byte[] BuildReadRandomWordCommand(McAddressData[] address) {
        byte[] command = new byte[6 + address.length * 4];
        command[0] = 0x03;                                                                  // 批量读取数据命令
        command[1] = 0x04;
        command[2] = 0x00;
        command[3] = 0x00;
        command[4] = (byte) address.length;                                                  // 访问的字点数
        command[5] = 0x00;                                                                  // 双字访问点数
        for (int i = 0; i < address.length; i++) {
            command[i * 4 + 6] = Utilities.getBytes(address[i].getAddressStart())[0];       // 软元件起始地址
            command[i * 4 + 7] = Utilities.getBytes(address[i].getAddressStart())[1];
            command[i * 4 + 8] = Utilities.getBytes(address[i].getAddressStart())[2];
            command[i * 4 + 9] = (byte) address[i].getMcDataType().getDataCode();                            // 软元件代号
        }
        return command;
    }

    /**
     * 随机读取的指令创建
     *
     * @param address 地址数组
     * @return 指令
     */
    public static byte[] BuildReadRandomCommand(McAddressData[] address) {
        byte[] command = new byte[6 + address.length * 6];
        command[0] = 0x06;                                                                  // 批量读取数据命令
        command[1] = 0x04;
        command[2] = 0x00;                                                                  // 子命令
        command[3] = 0x00;
        command[4] = (byte) address.length;                                                  // 字软元件的块数
        command[5] = 0x00;                                                                  // 位软元件的块数
        for (int i = 0; i < address.length; i++) {
            command[i * 6 + 6] = Utilities.getBytes(address[i].getAddressStart())[0];      // 字软元件的编号
            command[i * 6 + 7] = Utilities.getBytes(address[i].getAddressStart())[1];
            command[i * 6 + 8] = Utilities.getBytes(address[i].getAddressStart())[2];
            command[i * 6 + 9] = (byte) address[i].getMcDataType().getDataCode();                // 字软元件的代码
            command[i * 6 + 10] = (byte) (address[i].getLength() % 256);                          // 软元件的长度
            command[i * 6 + 11] = (byte) (address[i].getLength() / 256);
        }
        return command;
    }

    /**
     * 创建批量读取标签的报文数据信息
     * @param tags 标签名
     * @param lengths 长度信息
     * @return 报文名称
     * @throws Exception 异常信息
     */
    public static byte[] BuildReadTag(String[] tags, short[] lengths) throws Exception {
        if (tags.length != lengths.length) throw new Exception(StringResources.Language.TwoParametersLengthIsNotSame());

        ByteArrayOutputStream command = new ByteArrayOutputStream();
        command.write(0x1A);                                                          // 批量读取标签的指令
        command.write(0x04);
        command.write(0x00);                                                          // 子命令
        command.write(0x00);
        command.write(Utilities.getBytes(tags.length)[0]);                       // 排列点数
        command.write(Utilities.getBytes(tags.length)[1]);
        command.write(0x00);                                                          // 省略指定
        command.write(0x00);
        for (int i = 0; i < tags.length; i++) {
            byte[] tagBuffer = tags[i].getBytes(StandardCharsets.UTF_16);
            command.write(Utilities.getBytes(tagBuffer.length / 2)[0]);          // 标签长度
            command.write(Utilities.getBytes(tagBuffer.length / 2)[1]);
            command.write(tagBuffer, 0, tagBuffer.length);                          // 标签名称
            command.write(0x01);                                                      // 单位指定
            command.write(0x00);                                                      // 固定值
            command.write(Utilities.getBytes(lengths[i] * 2)[0]);                 // 排列数据长
            command.write(Utilities.getBytes(lengths[i] * 2)[1]);
        }
        byte[] buffer = command.toByteArray();
        command.close();
        return buffer;
    }

    /**
     * 读取本站缓冲寄存器的数据信息，需要指定寄存器的地址，和读取的长度
     *
     * @param address 寄存器的地址
     * @param length  数据长度
     * @return 结果内容
     */
    public static OperateResultExOne<byte[]> BuildReadMemoryCommand(String address, short length) {
        try {
            int add = Integer.parseInt(address);
            byte[] command = new byte[8];
            command[0] = 0x13;                                                      // 读取缓冲数据命令
            command[1] = 0x06;
            command[2] = Utilities.getBytes(add)[0];                           // 起始地址的地位
            command[3] = Utilities.getBytes(add)[1];
            command[4] = Utilities.getBytes(add)[2];
            command[5] = Utilities.getBytes(add)[3];
            command[6] = (byte) (length % 256);                                      // 软元件的长度
            command[7] = (byte) (length / 256);

            return OperateResultExOne.CreateSuccessResult(command);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 构建读取智能模块的命令，需要指定模块编号，起始地址，读取的长度，注意，该长度以字节为单位。
     * @param module 模块编号
     * @param address 智能模块的起始地址
     * @param length 读取的字长度
     * @return 报文的结果内容
     */
    public static OperateResultExOne<byte[]> BuildReadSmartModule( short module, String address, short length ) {
        try {
            int add = Integer.parseInt(address);
            byte[] command = new byte[12];
            command[0] = 0x01;                                                      // 读取智能缓冲数据命令
            command[1] = 0x06;
            command[2] = 0x00;
            command[3] = 0x00;
            command[4] = Utilities.getBytes(add)[0];                           // 起始地址的地位
            command[5] = Utilities.getBytes(add)[1];
            command[6] = Utilities.getBytes(add)[2];
            command[7] = Utilities.getBytes(add)[3];
            command[8] = (byte) (length % 256);                                      // 地址的长度
            command[9] = (byte) (length / 256);
            command[10] = Utilities.getBytes(module)[0];                        // 模块号
            command[11] = Utilities.getBytes(module)[1];
            return OperateResultExOne.CreateSuccessResult(command);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 解析出标签读取的数据内容
     *
     * @param content 返回的数据信息
     * @return 解析结果
     */
    public static OperateResultExOne<byte[]> ExtraTagData(byte[] content) {
        try {
            int count = Utilities.getUShort(content, 0);
            int index = 2;
            ArrayList<Byte> array = new ArrayList<Byte>(20);
            for (int i = 0; i < count; i++) {
                int length = Utilities.getUShort(content, index + 2);
                Utilities.ArrayListAddArray(array, SoftBasic.BytesArraySelectMiddle(content, index + 4, length));
                index += 4 + length;
            }
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(array));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    /**
     * 读取PLC的标签信息，需要传入标签的名称，读取的字长度，标签举例：A; label[1]; bbb[10,10,10]<br />
     * To read the label information of the PLC, you need to pass in the name of the label,
     * the length of the word read, and an example of the label: A; label [1]; bbb [10,10,10]
     * @param tags 标签名
     * @param length 读取长度
     * @return 是否成功
     */
    public static OperateResultExOne<byte[]> ReadTags(IReadWriteMc mc, String[] tags, short[] length ) {
        byte[] coreResult = new byte[0];
        try {
            coreResult = BuildReadTag(tags, length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 核心交互
        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
        if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

        return ExtraTagData(mc.ExtractActualData(read.Content, false));
    }

    public static byte[] ExtractActualDataHelper( byte[] response, boolean isBit ) {
        if (isBit) {
            // 位读取
            byte[] Content = new byte[response.length * 2];
            for (int i = 0; i < response.length; i++) {
                if ((response[i] & 0x10) == 0x10) {
                    Content[i * 2 + 0] = 0x01;
                }

                if ((response[i] & 0x01) == 0x01) {
                    Content[i * 2 + 1] = 0x01;
                }
            }

            return Content;
        } else {
            // 字读取
            return response;
        }
    }
}
