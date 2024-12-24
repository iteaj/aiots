package com.iteaj.plugin.protocol.plc.core.profinet.melsec.helper;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.address.McAddressData;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.profinet.melsec.MelsecHelper;

import java.nio.charset.StandardCharsets;


public class McAsciiHelper {

    /**
     * 从三菱地址，是否位读取进行创建读取Ascii格式的MC的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param isBit       是否进行了位读取操作
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildAsciiReadMcCoreCommand(McAddressData addressData, boolean isBit) {
        try {
            byte[] command = new byte[20];
            command[0] = 0x30;                                                               // 批量读取数据命令
            command[1] = 0x34;
            command[2] = 0x30;
            command[3] = 0x31;
            command[4] = 0x30;                                                               // 以点为单位还是字为单位成批读取
            command[5] = 0x30;
            command[6] = 0x30;
            command[7] = isBit ? (byte) 0x31 : (byte) 0x30;
            command[8] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];          // 软元件类型
            command[9] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
            command[10] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0];            // 起始地址的地位
            command[11] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1];
            command[12] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2];
            command[13] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3];
            command[14] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4];
            command[15] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5];
            command[16] = SoftBasic.BuildAsciiBytesFrom((short) addressData.getLength())[0];                                             // 软元件点数
            command[17] = SoftBasic.BuildAsciiBytesFrom((short) addressData.getLength())[1];
            command[18] = SoftBasic.BuildAsciiBytesFrom((short) addressData.getLength())[2];
            command[19] = SoftBasic.BuildAsciiBytesFrom((short) addressData.getLength())[3];

            return command;

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 以字为单位，创建ASCII数据写入的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param value       实际的原始数据信息
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildAsciiWriteWordCoreCommand(McAddressData addressData, byte[] value) {
        value = MelsecHelper.TransByteArrayToAsciiByteArray(value);

        byte[] command = new byte[20 + value.length];
        command[0] = 0x31;                                                                                         // 批量写入的命令
        command[1] = 0x34;
        command[2] = 0x30;
        command[3] = 0x31;
        command[4] = 0x30;                                                                                         // 子命令
        command[5] = 0x30;
        command[6] = 0x30;
        command[7] = 0x30;
        command[8] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];               // 软元件类型
        command[9] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
        command[10] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0];    // 起始地址的地位
        command[11] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1];
        command[12] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2];
        command[13] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3];
        command[14] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4];
        command[15] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5];
        command[16] = SoftBasic.BuildAsciiBytesFrom((short) (value.length / 4))[0];                               // 软元件点数
        command[17] = SoftBasic.BuildAsciiBytesFrom((short) (value.length / 4))[1];
        command[18] = SoftBasic.BuildAsciiBytesFrom((short) (value.length / 4))[2];
        command[19] = SoftBasic.BuildAsciiBytesFrom((short) (value.length / 4))[3];
        System.arraycopy(value, 0, command, 20, value.length);

        return command;
    }


    /**
     * 以位为单位，创建ASCII数据写入的核心报文
     *
     * @param addressData 三菱Mc协议的数据地址
     * @param value       原始的bool数组数据
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildAsciiWriteBitCoreCommand(McAddressData addressData, boolean[] value) {
        if (value == null) value = new boolean[0];
        byte[] buffer = new byte[value.length];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = value[i] ? (byte) 0x31 : (byte) 0x30;
        }

        byte[] command = new byte[20 + buffer.length];
        command[0] = 0x31;                                                                              // 批量写入的命令
        command[1] = 0x34;
        command[2] = 0x30;
        command[3] = 0x31;
        command[4] = 0x30;                                                                              // 子命令
        command[5] = 0x30;
        command[6] = 0x30;
        command[7] = 0x31;
        command[8] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];            // 软元件类型
        command[9] = (addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
        command[10] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0];     // 起始地址的地位
        command[11] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1];
        command[12] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2];
        command[13] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3];
        command[14] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4];
        command[15] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5];
        command[16] = SoftBasic.BuildAsciiBytesFrom((short) (value.length))[0];              // 软元件点数
        command[17] = SoftBasic.BuildAsciiBytesFrom((short) (value.length))[1];
        command[18] = SoftBasic.BuildAsciiBytesFrom((short) (value.length))[2];
        command[19] = SoftBasic.BuildAsciiBytesFrom((short) (value.length))[3];
        System.arraycopy(buffer, 0, command, 20, buffer.length);

        return command;
    }


    /**
     * 按字为单位随机读取的指令创建
     *
     * @param address 地址数组
     * @return 指令
     */
    public static byte[] BuildAsciiReadRandomWordCommand(McAddressData[] address) {
        byte[] command = new byte[12 + address.length * 8];
        command[0] = 0x30;                                                               // 批量读取数据命令
        command[1] = 0x34;
        command[2] = 0x30;
        command[3] = 0x33;
        command[4] = 0x30;                                                               // 以点为单位还是字为单位成批读取
        command[5] = 0x30;
        command[6] = 0x30;
        command[7] = 0x30;
        command[8] = SoftBasic.BuildAsciiBytesFrom((byte) address.length)[0];
        command[9] = SoftBasic.BuildAsciiBytesFrom((byte) address.length)[1];
        command[10] = 0x30;
        command[11] = 0x30;
        for (int i = 0; i < address.length; i++) {
            command[i * 8 + 12] = (address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];            // 软元件类型
            command[i * 8 + 13] = (address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
            command[i * 8 + 14] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[0];            // 起始地址的地位
            command[i * 8 + 15] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[1];
            command[i * 8 + 16] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[2];
            command[i * 8 + 17] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[3];
            command[i * 8 + 18] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[4];
            command[i * 8 + 19] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[5];
        }
        return command;
    }

    /**
     * 随机读取的指令创建
     * @param address 地址数组
     * @return 指令
     */
    public static byte[] BuildAsciiReadRandomCommand(McAddressData[] address) {
        byte[] command = new byte[12 + address.length * 12];
        command[0] = 0x30;                                                               // 批量读取数据命令
        command[1] = 0x34;
        command[2] = 0x30;
        command[3] = 0x36;
        command[4] = 0x30;                                                               // 以点为单位还是字为单位成批读取
        command[5] = 0x30;
        command[6] = 0x30;
        command[7] = 0x30;
        command[8] = SoftBasic.BuildAsciiBytesFrom((byte) address.length)[0];
        command[9] = SoftBasic.BuildAsciiBytesFrom((byte) address.length)[1];
        command[10] = 0x30;
        command[11] = 0x30;
        for (int i = 0; i < address.length; i++) {
            command[i * 12 + 12] = (address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[0];            // 软元件类型
            command[i * 12 + 13] = (address[i].getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII))[1];
            command[i * 12 + 14] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[0];            // 起始地址的地位
            command[i * 12 + 15] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[1];
            command[i * 12 + 16] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[2];
            command[i * 12 + 17] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[3];
            command[i * 12 + 18] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[4];
            command[i * 12 + 19] = MelsecHelper.BuildBytesFromAddress(address[i].getAddressStart(), address[i].getMcDataType())[5];
            command[i * 12 + 20] = SoftBasic.BuildAsciiBytesFrom((short) address[i].getLength())[0];
            command[i * 12 + 21] = SoftBasic.BuildAsciiBytesFrom((short) address[i].getLength())[1];
            command[i * 12 + 22] = SoftBasic.BuildAsciiBytesFrom((short) address[i].getLength())[2];
            command[i * 12 + 23] = SoftBasic.BuildAsciiBytesFrom((short) address[i].getLength())[3];
        }
        return command;
    }


    public static OperateResultExOne<byte[]> BuildAsciiReadMemoryCommand(String address, short length ) {
        try {
            int add = Integer.parseInt(address);
            byte[] command = new byte[20];
            command[0] = 0x30;                                                      // 读取缓冲数据命令
            command[1] = 0x36;
            command[2] = 0x31;
            command[3] = 0x33;
            command[4] = 0x30;
            command[5] = 0x30;
            command[6] = 0x30;
            command[7] = 0x30;
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(add), 0, command, 8, 8);              // 起始地址信息
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(length), 0, command, 16, 4);          // 软元件的长度

            return OperateResultExOne.CreateSuccessResult(command);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildAsciiReadSmartModule( short module, String address, short length ) {
        try {
            int add = Integer.parseInt(address);
            byte[] command = new byte[24];
            command[0] = 0x30;                                                          // 读取智能缓冲数据命令
            command[1] = 0x36;
            command[2] = 0x30;
            command[3] = 0x31;
            command[4] = 0x30;
            command[5] = 0x30;
            command[6] = 0x30;
            command[7] = 0x30;
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(add), 0, command, 8, 8);      // 起始地址的地位
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(length), 0, command, 16, 4); // 地址的长度
            System.arraycopy(SoftBasic.BuildAsciiBytesFrom(module), 0, command, 20, 4);  // 模块号
            return OperateResultExOne.CreateSuccessResult(command);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 从三菱扩展地址，是否位读取进行创建读取的MC的核心报文
     * @param addressData  三菱Mc协议的数据地址
     * @param extend 扩展指定
     * @param isBit 是否进行了位读取操作
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildAsciiReadMcCoreExtendCommand( McAddressData addressData, short extend, boolean isBit ) {
        byte[] command = new byte[32];
        command[0] = 0x30;                                                               // 批量读取数据命令
        command[1] = 0x34;
        command[2] = 0x30;
        command[3] = 0x31;
        command[4] = 0x30;                                                               // 以点为单位还是字为单位成批读取
        command[5] = 0x30;
        command[6] = 0x38;
        command[7] = isBit ? (byte) 0x31 : (byte) 0x30;
        command[8] = 0x30;
        command[9] = 0x30;
        command[10] = 0x4A;                                                              // 扩展指定
        command[11] = SoftBasic.BuildAsciiBytesFrom(extend)[1];
        command[12] = SoftBasic.BuildAsciiBytesFrom(extend)[2];
        command[13] = SoftBasic.BuildAsciiBytesFrom(extend)[3];
        command[14] = 0x30;
        command[15] = 0x30;
        command[16] = 0x30;
        command[17] = addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[0];          // 软元件类型
        command[18] = addressData.getMcDataType().getAsciiCode().getBytes(StandardCharsets.US_ASCII)[1];
        command[19] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[0];            // 起始地址的地位
        command[20] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[1];
        command[21] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[2];
        command[22] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[3];
        command[23] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[4];
        command[24] = MelsecHelper.BuildBytesFromAddress(addressData.getAddressStart(), addressData.getMcDataType())[5];
        command[25] = 0x30;
        command[26] = 0x30;
        command[27] = 0x30;
        command[28] = SoftBasic.BuildAsciiBytesFrom(addressData.getLength())[0];                                             // 软元件点数
        command[29] = SoftBasic.BuildAsciiBytesFrom(addressData.getLength())[1];
        command[30] = SoftBasic.BuildAsciiBytesFrom(addressData.getLength())[2];
        command[31] = SoftBasic.BuildAsciiBytesFrom(addressData.getLength())[3];

        return command;
    }

    /**
     * 将MC协议的核心报文打包成一个可以直接对PLC进行发送的原始报文
     * @param mcCore MC协议的核心报文
     * @param networkNumber 网络号
     * @param networkStationNumber 网络站号
     * @return 原始报文信息
     */
    public static byte[] PackMcCommand( byte[] mcCore, byte networkNumber, byte networkStationNumber )
    {
        byte[] plcCommand = new byte[22 + mcCore.length];
        plcCommand[ 0] = 0x35;                                                                        // 副标题
        plcCommand[ 1] = 0x30;
        plcCommand[ 2] = 0x30;
        plcCommand[ 3] = 0x30;
        plcCommand[ 4] = MelsecHelper.BuildBytesFromData( networkNumber )[0];                         // 网络号
        plcCommand[ 5] = MelsecHelper.BuildBytesFromData( networkNumber )[1];
        plcCommand[ 6] = 0x46;                                                                        // PLC编号
        plcCommand[ 7] = 0x46;
        plcCommand[ 8] = 0x30;                                                                        // 目标模块IO编号
        plcCommand[ 9] = 0x33;
        plcCommand[10] = 0x46;
        plcCommand[11] = 0x46;
        plcCommand[12] = MelsecHelper.BuildBytesFromData( networkStationNumber )[0];                  // 目标模块站号
        plcCommand[13] = MelsecHelper.BuildBytesFromData( networkStationNumber )[1];
        plcCommand[14] = MelsecHelper.BuildBytesFromData( (short)(plcCommand.length - 18) )[0];     // 请求数据长度
        plcCommand[15] = MelsecHelper.BuildBytesFromData( (short)(plcCommand.length - 18) )[1];
        plcCommand[16] = MelsecHelper.BuildBytesFromData( (short)(plcCommand.length - 18) )[2];
        plcCommand[17] = MelsecHelper.BuildBytesFromData( (short)(plcCommand.length - 18) )[3];
        plcCommand[18] = 0x30;                                                                        // CPU监视定时器
        plcCommand[19] = 0x30;
        plcCommand[20] = 0x31;
        plcCommand[21] = 0x30;
        System.arraycopy(mcCore, 0, plcCommand, 22, mcCore.length);

        return plcCommand;
    }

    /**
     * 从PLC反馈的数据中提取出实际的数据内容，需要传入反馈数据，是否位读取
     * @param response 反馈的数据内容
     * @param isBit 是否位读取
     * @return 解析后的结果对象
     */
    public static byte[] ExtractActualDataHelper( byte[] response, boolean isBit )
    {
        if (isBit)
        {
            // 位读取
            byte[] Content = new byte[response.length];
            for (int i = 0; i < response.length; i++)
            {
                if (response[i] == 0x30)
                {
                    Content[i] = 0x00;
                }
                else
                {
                    Content[i] = 0x01;
                }
            }

            return Content;
        }
        else
        {
            return  MelsecHelper.TransAsciiByteArrayToByteArray(response);
        }
    }


    /**
     * 检查反馈的内容是否正确的
     * @param content MC的反馈的内容
     * @return 是否正确
     */
    public static OperateResult CheckResponseContentHelper(byte[] content ) {
        int errorCode = Integer.parseInt(new String(SoftBasic.BytesArraySelectMiddle(content, 18, 4), StandardCharsets.US_ASCII), 16);
        if (errorCode != 0) return new OperateResult(errorCode, MelsecHelper.GetErrorDescription(errorCode));

        return OperateResult.CreateSuccessResult();
    }
}
