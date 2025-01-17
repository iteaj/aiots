package com.iteaj.plugin.protocol.plc.core.profinet.Fuji;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.address.FujiSPBAddress;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.StringResources;

import java.nio.charset.StandardCharsets;

public class FujiSPBHelper {

    // region Static Helper

    /**
     * 将int数据转换成SPB可识别的标准的数据内容，例如 2转换为0200 , 200转换为0002
     * @param address 等待转换的数据内容
     * @return 转换之后的数据内容
     */
    public static String AnalysisIntegerAddress( int address ) {
        String tmp = String.format("%04d", address);
        return tmp.substring(2) + tmp.substring(0, 2);
    }

    /**
     * 计算指令的和校验码
     * @param data 指令
     * @return 校验之后的信息
     */
    public static String CalculateAcc( String data ) {
        byte[] buffer = data.getBytes(StandardCharsets.US_ASCII);

        int count = 0;
        for (int i = 0; i < buffer.length; i++) {
            count += buffer[i];
        }

        return String.format("%04X", count).substring(2);
    }

    /**
     * 创建一条读取的指令信息，需要指定一些参数，单次读取最大105个字
     * @param station PLC的站号
     * @param address 地址信息
     * @param length 数据长度
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> BuildReadCommand(byte station, String address, short length ) {
        OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address, "s", station);
        station = analysis.Content1.byteValue();
        address = analysis.Content2;

        OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        return BuildReadCommand(station, addressAnalysis.Content, length);
    }

    /**
     * 创建一条读取的指令信息，需要指定一些参数，单次读取最大105个字
     * @param station PLC的站号
     * @param address 地址信息
     * @param length 数据长度
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> BuildReadCommand( byte station, FujiSPBAddress address, short length ) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("09");
        stringBuilder.append("FFFF");
        stringBuilder.append("00");
        stringBuilder.append("00");
        stringBuilder.append(address.GetWordAddress());
        stringBuilder.append(AnalysisIntegerAddress(length));
        stringBuilder.append("\r\n");
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 创建一条读取多个地址的指令信息，需要指定一些参数，单次读取最大105个字
     * @param station PLC的站号
     * @param address 地址信息
     * @param length 数据长度
     * @param isBool 是否位读取
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> BuildReadCommand( byte station, String[] address, short[] length, boolean isBool ) {
        if (address == null || length == null)
            return new OperateResultExOne<byte[]>("Parameter address or length can't be null");
        if (address.length != length.length)
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append(String.format("%02X", (6 + address.length * 4)));
        stringBuilder.append("FFFF");
        stringBuilder.append("00");
        stringBuilder.append("04");
        stringBuilder.append("00");
        stringBuilder.append(String.format("%02X", address.length));
        for (int i = 0; i < address.length; i++) {
            OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address[i], "s", station);
            station = analysis.Content1.byteValue();
            address[i] = analysis.Content2;

            OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address[i]);
            if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

            stringBuilder.append(addressAnalysis.Content.getTypeCode());
            stringBuilder.append(String.format("%02X", length[i]));
            stringBuilder.append(AnalysisIntegerAddress(addressAnalysis.Content.getAddressStart()));
        }
        stringBuilder.setCharAt(1, String.format("%02X", station).charAt(0));
        stringBuilder.setCharAt(2, String.format("%02X", station).charAt(1));
        stringBuilder.append("\r\n");
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 创建一条别入byte数据的指令信息，需要指定一些参数，按照字单位，单次写入最大103个字
     * @param station 站号
     * @param address 地址
     * @param value 数组值
     * @return 是否创建成功
     */
    public static OperateResultExOne<byte[]> BuildWriteByteCommand( byte station, String address, byte[] value ) {
        OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address, "s", station);
        station = analysis.Content1.byteValue();
        address = analysis.Content2;

        OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("00");
        stringBuilder.append("FFFF");
        stringBuilder.append("01");
        stringBuilder.append("00");
        stringBuilder.append(addressAnalysis.Content.GetWordAddress());
        stringBuilder.append(AnalysisIntegerAddress(value.length / 2));
        stringBuilder.append(SoftBasic.ByteToHexString(value));

        stringBuilder.setCharAt(3, String.format("%02X", (stringBuilder.length() - 5) / 2).charAt(0));
        stringBuilder.setCharAt(4, String.format("%02X", (stringBuilder.length() - 5) / 2).charAt(1));
        stringBuilder.append("\r\n");
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 创建一条别入byte数据的指令信息，需要指定一些参数，按照字单位，单次写入最大103个字
     * @param station 站号
     * @param address 地址
     * @param value 数组值
     * @return 是否创建成功
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolCommand( byte station, String address, boolean value ) {
        OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address, "s", station);
        station = analysis.Content1.byteValue();
        address = analysis.Content2;

        OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        if (address.startsWith("X") ||
                address.startsWith("Y") ||
                address.startsWith("M") ||
                address.startsWith("L") ||
                address.startsWith("TC") ||
                address.startsWith("CC")) {
            if (address.indexOf('.') < 0) {
                // 当是M1000这种地址的时候，需要进行转换一下字地址
                addressAnalysis.Content.setBitIndex(addressAnalysis.Content.getAddressStart() % 16);
                addressAnalysis.Content.setAddressStart(addressAnalysis.Content.getAddressStart() / 16);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02X", station));
        stringBuilder.append("00");
        stringBuilder.append("FFFF");
        stringBuilder.append("01");
        stringBuilder.append("02");
        stringBuilder.append(addressAnalysis.Content.GetWriteBoolAddress());
        stringBuilder.append(value ? "01" : "00");

        stringBuilder.setCharAt(3, String.format("%02X", (stringBuilder.length() - 5) / 2).charAt(0));
        stringBuilder.setCharAt(4, String.format("%02X", (stringBuilder.length() - 5) / 2).charAt(1));
        stringBuilder.append("\r\n");
        return OperateResultExOne.CreateSuccessResult(stringBuilder.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 检查反馈的数据信息，是否包含了错误码，如果没有包含，则返回成功
     * @param content 原始的报文返回
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> CheckResponseData( byte[] content ) {
        if (content[0] != ':')
            return new OperateResultExOne<byte[]>(content[0], "Read Faild:" + SoftBasic.ByteToHexString(content, ' '));
        String code = new String(content, 9, 2, StandardCharsets.US_ASCII);

        if (!code.equals("00"))
            return new OperateResultExOne<byte[]>(Integer.parseInt(code, 16), GetErrorDescriptionFromCode(code));
        if (content[content.length - 2] == 0x0D && content[content.length - 1] == 0x0A)
            content = SoftBasic.BytesArrayRemoveLast(content, 2);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 11));
    }

    /**
     * 根据错误码获取到真实的文本信息
     * @param code 错误码
     * @return 错误的文本描述
     */
    public static String GetErrorDescriptionFromCode( String code ) {
        switch (code) {
            case "01":
                return StringResources.Language.FujiSpbStatus01();
            case "02":
                return StringResources.Language.FujiSpbStatus02();
            case "03":
                return StringResources.Language.FujiSpbStatus03();
            case "04":
                return StringResources.Language.FujiSpbStatus04();
            case "05":
                return StringResources.Language.FujiSpbStatus05();
            case "06":
                return StringResources.Language.FujiSpbStatus06();
            case "07":
                return StringResources.Language.FujiSpbStatus07();
            case "09":
                return StringResources.Language.FujiSpbStatus09();
            case "0C":
                return StringResources.Language.FujiSpbStatus0C();
            default:
                return StringResources.Language.UnknownError();
        }
    }

    // endregion

    /**
     * 批量读取PLC的数据，以字为单位，支持读取X,Y,L,M,D,TN,CN,TC,CC,R,W具体的地址范围需要根据PLC型号来确认，地址可以携带站号信息，例如：s=2;D100<br />
     * Read PLC data in batches, in units of words. Supports reading X, Y, L, M, D, TN, CN, TC, CC, R, W.
     * The specific address range needs to be confirmed according to the PLC model, The address can carry station number information, for example: s=2;D100
     * @param device PLC设备通信对象
     * @param station 当前的站号信息
     * @param address 地址信息
     * @param length 数据长度
     * @return 读取结果信息
     */
    public static OperateResultExOne<byte[]> Read(IReadWriteDevice device, byte station, String address, short length ) {
        // 解析指令
        OperateResultExOne<byte[]> command = BuildReadCommand(station, address, length);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心交互
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 结果验证
        OperateResultExOne<byte[]> check = CheckResponseData(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 提取结果
        return OperateResultExOne.CreateSuccessResult(SoftBasic.HexStringToBytes(new String(SoftBasic.BytesArrayRemoveBegin(check.Content, 4), StandardCharsets.US_ASCII)));
    }

    /**
     * 批量写入PLC的数据，以字为单位，也就是说最少2个字节信息，支持读取X,Y,L,M,D,TN,CN,TC,CC,R具体的地址范围需要根据PLC型号来确认，地址可以携带站号信息，例如：s=2;D100<br />
     * The data written to the PLC in batches, in units of words, that is, a minimum of 2 bytes of information. It supports reading X, Y, L, M, D, TN, CN, TC, CC, and R.
     * The specific address range needs to be based on PLC model to confirm, The address can carry station number information, for example: s=2;D100
     * @param device PLC设备通信对象
     * @param station 当前的站号信息
     * @param address 地址信息，举例，D100，R200，TN100，CN200
     * @param value 数据值
     * @return 是否写入成功
     */
    public static OperateResult Write(IReadWriteDevice device, byte station, String address, byte[] value ) {
        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteByteCommand(station, address, value);
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        return CheckResponseData(read.Content);
    }

    /**
     * 批量读取PLC的Bool数据，以位为单位，支持读取X,Y,L,M,D,TN,CN,TC,CC,R,W，例如 M100, 如果是寄存器地址，可以使用D10.12来访问第10个字的12位，地址可以携带站号信息，例如：s=2;M100<br />
     * Read PLC's Bool data in batches, in units of bits, support reading X, Y, L, M, D, TN, CN, TC, CC, R, W, such as M100, if it is a register address,
     * you can use D10. 12 to access the 12 bits of the 10th word, the address can carry station number information, for example: s=2;M100
     * @param device PLC设备通信对象
     * @param station 当前的站号信息
     * @param address 地址信息，举例：M100, D10.12
     * @param length 读取的bool长度信息
     * @return Bool[]的结果对象
     */
    public static OperateResultExOne<boolean[]> ReadBool( IReadWriteDevice device, byte station, String address, short length ) {
        OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address, "s", station);
        station = analysis.Content1.byteValue();
        address = analysis.Content2;


        OperateResultExOne<FujiSPBAddress> addressAnalysis = FujiSPBAddress.ParseFrom(address);
        if (!addressAnalysis.IsSuccess) return OperateResultExOne.CreateFailedResult(addressAnalysis);

        if (address.startsWith("X") ||
                address.startsWith("Y") ||
                address.startsWith("M") ||
                address.startsWith("L") ||
                address.startsWith("TC") ||
                address.startsWith("CC")) {
            if (address.indexOf('.') < 0) {
                // 当是M1000这种地址的时候，需要进行转换一下字地址
                addressAnalysis.Content.setBitIndex(addressAnalysis.Content.getAddressStart() % 16);
                addressAnalysis.Content.setAddressStart(addressAnalysis.Content.getAddressStart() / 16);
            }
        }

        short len = (short) ((addressAnalysis.Content.GetBitIndex() + length - 1) / 16 - addressAnalysis.Content.GetBitIndex() / 16 + 1);
        // 解析指令
        OperateResultExOne<byte[]> command = BuildReadCommand(station, addressAnalysis.Content, len);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 核心交互
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 结果验证
        OperateResultExOne<byte[]> check = CheckResponseData(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        // 提取结果
        return OperateResultExOne.CreateSuccessResult(
                SoftBasic.BoolArraySelectMiddle(
                        SoftBasic.ByteToBoolArray(
                                SoftBasic.HexStringToBytes(
                                        new String(SoftBasic.BytesArrayRemoveBegin(check.Content, 4)))),
                        addressAnalysis.Content.getBitIndex(), length));
    }

    /**
     * 写入一个Bool值到一个地址里，地址可以是线圈地址，也可以是寄存器地址，例如：M100, D10.12，地址可以携带站号信息，例如：s=2;D10.12<br />
     * Write a Bool value to an address. The address can be a coil address or a register address, for example: M100, D10.12.
     * The address can carry station number information, for example: s=2;D10.12
     * @param device PLC设备通信对象
     * @param station 当前的站号信息
     * @param address 地址信息，举例：M100, D10.12
     * @param value 写入的bool值
     * @return 是否写入成功的结果对象
     */
    public static OperateResult Write(IReadWriteDevice device, byte station, String address, boolean value ) {
        // 解析指令
        OperateResultExOne<byte[]> command = BuildWriteBoolCommand(station, address, value);
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 结果验证
        return CheckResponseData(read.Content);
    }

    //public OperateResult<byte[]> ReadRandom( string[] address, ushort[] length )
    //{
    //	// 解析指令
    //	OperateResult<byte[]> command = BuildReadCommand( this.station, address, length, false );
    //	if (!command.IsSuccess) return OperateResult.CreateFailedResult<byte[]>( command );

    //	// 核心交互
    //	OperateResult<byte[]> read = ReadFromCoreServer( command.Content );
    //	if (!read.IsSuccess) return OperateResult.CreateFailedResult<byte[]>( read );

    //	// 结果验证
    //	if (read.Content[0] != ':') return new OperateResult<byte[]>( read.Content[0], "Read Faild:" + SoftBasic.ByteToHexString( read.Content, ' ' ) );
    //	if (Encoding.ASCII.GetString( read.Content, 9, 2 ) != "00") return new OperateResult<byte[]>( read.Content[5], GetErrorDescriptionFromCode( Encoding.ASCII.GetString( read.Content, 9, 2 ) ) );

    //	// 提取结果
    //	byte[] Content = new byte[length * 2];
    //	for (int i = 0; i < Content.Length / 2; i++)
    //	{
    //		ushort tmp = Convert.ToUInt16( Encoding.ASCII.GetString( read.Content, i * 4 + 6, 4 ), 16 );
    //		BitConverter.GetBytes( tmp ).CopyTo( Content, i * 2 );
    //	}
    //	return OperateResult.CreateSuccessResult( Content );
    //}


}
