package com.iteaj.plugin.protocol.plc.melsec.network;


import com.iteaj.plugin.protocol.plc.core.net.ReadWriteNetHelper;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class McHelper {
    /**
     * 返回按照字单位读取的最低的长度信息
     * @param type MC协议的类型
     * @return 长度信息
     */
    public static int GetReadWordLength( McType type ) {
        if (type == McType.McBinary) return 950;
        return 460;
    }

    /**
     * 返回按照位单位读取的最低的长度信息
     * @param type MC协议的类型
     * @return 长度信息
     */
    public static int GetReadBoolLength( McType type ) {
        if (type == McType.McBinary) return 7168;
        return 3584;
    }

    /**
     * 初步支持普通的数据地址之外，还额外支持高级的地址写法，以下是示例（适用于MC协议为二进制和ASCII格式）：<br />
     * [商业授权] 扩展的数据地址: 表示为 ext=1;W100  访问扩展区域为1的W100的地址信息<br />
     * [商业授权] 缓冲存储器地址: 表示为 mem=32  访问地址为32的本站缓冲存储器地址<br />
     * [商业授权] 智能模块地址：表示为 module=3;4106  访问模块号3，偏移地址是4106的数据，偏移地址需要根据模块的详细信息来确认。<br />
     * [商业授权] 基于标签的地址: 表示位 s=AAA  假如标签的名称为AAA，但是标签的读取是有条件的，详细参照<see cref="McBinaryHelper.ReadTags(IReadWriteMc, string[], ushort[])"/>
     * @param mc mc协议对象
     * @param address 三菱的地址
     * @param length 读取的长度信息
     * @return 原始数据的结果对象信息
     */
    public static OperateResultExOne<byte[]> Read(IReadWriteMc mc, String address, short length ) {
        if (mc.getMcType() == McType.McBinary && address.startsWith("s=") || address.startsWith("S=")) {
            return McBinaryHelper.ReadTags(mc, new String[]{address.substring(2)}, new short[]{length});
        } else if ((mc.getMcType() == McType.McBinary || mc.getMcType() == McType.MCAscii) &&
                Pattern.matches("ext=[0-9]+;", address)) {
            String extStr = Pattern.compile("ext=[0-9]+;").matcher(address).group();
            short ext = Short.parseShort(Pattern.compile("[0-9]+").matcher(extStr).group());
            return ReadExtend(mc, ext, address.substring(extStr.length()), length);
        } else if ((mc.getMcType() == McType.McBinary || mc.getMcType() == McType.MCAscii) &&
                Pattern.matches("mem=", address)) {
            return ReadMemory(mc, address.substring(4), length);
        }
//        else if ((mc.getMcType() == McType.McBinary || mc.getMcType() == McType.MCAscii) &&
//                Pattern.matches("module=[0-9]+;", address))
//        {
//            String moduleStr = Regex.Match( address, "module=[0-9]+;" ).Value;
//            ushort module = ushort.Parse( Regex.Match( moduleStr, "[0-9]+" ).Value );
//            return ReadSmartModule( mc, module, address.Substring( moduleStr.Length ), (ushort)(length * 2) );
//        }
        else {
            // 分析地址
            OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, length, false);
            if (!addressResult.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(addressResult);

            ArrayList<Byte> bytesContent = new ArrayList<Byte>();
            int alreadyFinished = 0;
            while (alreadyFinished < length) {
                int readLength = Math.min(length - alreadyFinished, GetReadWordLength(mc.getMcType()));
                addressResult.Content.setLength(readLength);

                byte[] command =
                        mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadMcCoreCommand(addressResult.Content, false) :
                                mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadMcCoreCommand(addressResult.Content, false) :
                                        mc.getMcType() == McType.McRBinary ? MelsecMcRNet.BuildReadMcCoreCommand( addressResult.Content, false ) : null;

                OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(command);
                if (!read.IsSuccess) return read;

                Utilities.ArrayListAddArray(bytesContent, mc.ExtractActualData(read.Content, false));
                alreadyFinished += readLength;

                // 字的话就是正常的偏移位置，如果是位的话，就转到位的数据
                if (addressResult.Content.getMcDataType().getDataType() == 0)
                    addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength);
                else
                    addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength * 16);
            }
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bytesContent));
        }
    }

    public static OperateResult Write(IReadWriteMc mc, String address, byte[] value ) {
        // 分析地址
        OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, (short) 0, false);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        // 创建核心报文
        byte[] coreResult =
                mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildWriteWordCoreCommand(addressResult.Content, value) :
                        mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiWriteWordCoreCommand(addressResult.Content, value) :
                                mc.getMcType() == McType.McRBinary ? MelsecMcRNet.BuildWriteWordCoreCommand( addressResult.Content, value ) : null;

        // 核心交互
        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
        if (!read.IsSuccess) return read;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    public static OperateResultExOne<boolean[]> ReadBool( IReadWriteMc mc, String address, short length ) {
        return ReadBool(mc, address, length, true);
    }

    /**
     * 当读取的长度过大时，会自动进行切割，对于二进制格式，切割长度为7168，对于ASCII格式协议来说，切割长度则是3584
     *
     * 也支持对D100.0 这种地址进行位访问的功能
     */
    public static OperateResultExOne<boolean[]> ReadBool( IReadWriteMc mc, String address, short length, boolean supportWordAdd ) {
        if ( supportWordAdd && address.indexOf( '.' ) > 0) {
            return PlcHelper.ReadBool(mc, address, length);
        }
        else {
            // 分析地址
            OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, length, true);
            if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

            ArrayList<Boolean> boolContent = new ArrayList<>();
            short alreadyFinished = 0;
            while (alreadyFinished < length) {
                short readLength = (short) Math.min(length - alreadyFinished, GetReadBoolLength(mc.getMcType()));
                addressResult.Content.setLength(readLength);

                // 获取指令
                byte[] coreResult =
                        mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadMcCoreCommand(addressResult.Content, true) :
                                mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadMcCoreCommand(addressResult.Content, true) :
                                        mc.getMcType() == McType.McRBinary ? MelsecMcRNet.BuildReadMcCoreCommand( addressResult.Content, true ) : null;

                // 核心交互
                OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
                if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

                byte[] extra = mc.ExtractActualData(read.Content, true);
                for (int i = 0; i < readLength; i++) {
                    if (i < extra.length) {
                        boolContent.add(extra[i] == 0x01);
                    }
                }

                alreadyFinished += readLength;

                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength);
            }

            // 转化bool数组
            return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(boolContent));
        }
    }

    public static OperateResult Write( IReadWriteMc mc, String address, boolean[] values ) {
        return Write(mc, address, values, true);
    }

    public static OperateResult Write( IReadWriteMc mc, String address, boolean[] values, boolean supportWordAdd ) {
        if (supportWordAdd && address.indexOf(".") > 0) {
            return ReadWriteNetHelper.WriteBoolWithWord(mc, address, values, 16);
        } else {
            // 分析地址
            OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, (short) 0, true);
            if (!addressResult.IsSuccess) return addressResult;

            byte[] coreResult =
                    mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildWriteBitCoreCommand(addressResult.Content, values) :
                            mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiWriteBitCoreCommand(addressResult.Content, values) :
                                    mc.getMcType() == McType.McRBinary ? MelsecMcRNet.BuildWriteBitCoreCommand(addressResult.Content, values) : null;

            // 核心交互
            OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
            if (!read.IsSuccess) return read;

            // 成功
            return OperateResult.CreateSuccessResult();
        }
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，但是每个地址只能读取一个word，也就是2个字节的内容。收到结果后，需要自行解析数据<br />
     * Randomly read PLC data information, which can be combined across addresses and types, but each address can only read one word,
     * which is the content of 2 bytes. After receiving the results, you need to parse the data yourself
     * <br /><br />
     * 访问安装有 Q 系列 C24/E71 的站 QCPU 上位站 经由 Q 系列兼容网络系统 MELSECNET/H MELSECNET/10 Ethernet 的 QCPU 其他站 时访问点数········1≦ 字访问点数 双字访问点数 ≦192<br />
     * 访问 QnACPU 其他站 经由 QnA 系列兼容网络系统 MELSECNET/10 Ethernet 的 Q/QnACPU 其他站 时访问点数········1≦ 字访问点数 双字访问点数 ≦96<br />
     * 访问上述以外的 PLC CPU 其他站 时访问点数········1≦字访问点数≦10
     * @param mc MC协议通信对象
     * @param address 所有的地址的集合
     * @return 结果
     */
    public static OperateResultExOne<byte[]> ReadRandom( IReadWriteMc mc, String[] address ) {
        McAddressData[] mcAddressDatas = new McAddressData[address.length];
        for (int i = 0; i < address.length; i++) {
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address[i], 1);
            if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

            mcAddressDatas[i] = addressResult.Content;
        }

        byte[] coreResult =
                mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadRandomWordCommand(mcAddressDatas) :
                        mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadRandomWordCommand(mcAddressDatas) : null;

        // 核心交互
        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 数据解析，需要传入是否使用位的参数
        return OperateResultExOne.CreateSuccessResult(mc.ExtractActualData(read.Content, false));
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，每个地址是任意的长度。收到结果后，需要自行解析数据，目前只支持字地址，比如D区，W区，R区，不支持X，Y，M，B，L等等<br />
     * Read the data information of the PLC randomly. It can be combined across addresses and types. Each address is of any length. After receiving the results,
     * you need to parse the data yourself. Currently, only word addresses are supported, such as D area, W area, R area. X, Y, M, B, L, etc
     * <br /><br />
     * 实际测试不一定所有的plc都可以读取成功，具体情况需要具体分析<br />
     * 1 块数按照下列要求指定 120 ≧ 字软元件块数 + 位软元件块数<br />
     * 2 各软元件点数按照下列要求指定 960 ≧ 字软元件各块的合计点数 + 位软元件各块的合计点数
     * @param mc MC协议通信对象
     * @param address 所有的地址的集合
     * @param length 每个地址的长度信息
     * @return 结果
     */
    public static OperateResultExOne<byte[]> ReadRandom( IReadWriteMc mc, String[] address, short[] length ) {
        if (length.length != address.length)
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());

        McAddressData[] mcAddressDatas = new McAddressData[address.length];
        for (int i = 0; i < address.length; i++) {
            OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address[i], length[i]);
            if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

            mcAddressDatas[i] = addressResult.Content;
        }

        byte[] coreResult =
                mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadRandomCommand(mcAddressDatas) :
                        mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadRandomCommand(mcAddressDatas) : null;

        // 核心交互
        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 数据解析，需要传入是否使用位的参数
        return OperateResultExOne.CreateSuccessResult(mc.ExtractActualData(read.Content, false));
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，但是每个地址只能读取一个word，也就是2个字节的内容。收到结果后，自动转换为了short类型的数组<br />
     * Randomly read PLC data information, which can be combined across addresses and types, but each address can only read one word,
     * which is the content of 2 bytes. After receiving the result, it is automatically converted to an array of type short.
     * <br /><br />
     * 访问安装有 Q 系列 C24/E71 的站 QCPU 上位站 经由 Q 系列兼容网络系统 MELSECNET/H MELSECNET/10 Ethernet 的 QCPU 其他站 时
     * 访问点数········1≦ 字访问点数 双字访问点数 ≦192<br />
     * 访问 QnACPU 其他站 经由 QnA 系列兼容网络系统 MELSECNET/10 Ethernet 的 Q/QnACPU 其他站 时访问点数········1≦ 字访问点数 双字访问点数 ≦96<br />
     * 访问上述以外的 PLC CPU 其他站 时访问点数········1≦字访问点数≦10
     * @param mc MC协议的通信对象
     * @param address 所有的地址的集合
     * @return 包含是否成功的结果对象
     */
    public static OperateResultExOne<short[]> ReadRandomInt16( IReadWriteMc mc, String[] address ) {
        OperateResultExOne<byte[]> read = ReadRandom(mc, address);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(mc.getByteTransform().TransInt16(read.Content, 0, address.length));
    }

    /**
     * 随机读取PLC的数据信息，可以跨地址，跨类型组合，但是每个地址只能读取一个word，也就是2个字节的内容。收到结果后，自动转换为了ushort类型的数组<br />
     * Randomly read PLC data information, which can be combined across addresses and types, but each address can only read one word,
     * which is the content of 2 bytes. After receiving the result, it is automatically converted to an array of type ushort.
     * <br /><br />
     * 访问安装有 Q 系列 C24/E71 的站 QCPU 上位站 经由 Q 系列兼容网络系统 MELSECNET/H MELSECNET/10 Ethernet 的 QCPU 其他站 时访问点数········1≦ 字访问点数 双字访问点数 ≦192
     * <br />
     * 访问 QnACPU 其他站 经由 QnA 系列兼容网络系统 MELSECNET/10 Ethernet 的 Q/QnACPU 其他站 时访问点数········1≦ 字访问点数 双字访问点数 ≦96
     * <br />
     * 访问上述以外的 PLC CPU 其他站 时访问点数········1≦字访问点数≦10
     * @param mc MC协议的通信对象
     * @param address 所有的地址的集合
     * @return 包含是否成功的结果对象
     */
    public static OperateResultExOne<int[]> ReadRandomUInt16( IReadWriteMc mc, String[] address ) {
        OperateResultExOne<byte[]> read = ReadRandom(mc, address);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(mc.getByteTransform().TransUInt16(read.Content, 0, address.length));
    }

    /**
     * <b>[商业授权]</b> 读取缓冲寄存器的数据信息，地址直接为偏移地址<br />
     * <b>[Authorization]</b> Read the data information of the buffer register, the address is directly the offset address
     * <br /><br />
     * 本指令不可以访问下述缓冲存储器:<br />
     * 1. 本站(SLMP对应设备)上安装的智能功能模块<br />
     * 2. 其它站缓冲存储器<br />
     * @param mc MC通信对象
     * @param address 偏移地址
     * @param length 读取长度
     * @return 读取的内容
     */
    public static OperateResultExOne<byte[]> ReadMemory( IReadWriteMc mc, String address, short length ) {

        OperateResultExOne<byte[]> coreResult =
                mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadMemoryCommand(address, length) :
                        mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadMemoryCommand(address, length) : null;
        if (!coreResult.IsSuccess) return coreResult;

        // 核心交互
        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 数据初级解析，需要传入是否使用位的参数
        return OperateResultExOne.CreateSuccessResult(mc.ExtractActualData(read.Content, false));
    }

    /**
     * <b>[商业授权]</b> 读取智能模块的数据信息，需要指定模块地址，偏移地址，读取的字节长度<br />
     * <b>[Authorization]</b> To read the extended data information, you need to enter the extended value information in addition to the original address and length information
     * @param mc MC通信对象
     * @param module 模块地址
     * @param address 地址
     * @param length 数据长度
     * @return 返回结果
     */
    public static OperateResultExOne<byte[]> ReadSmartModule( IReadWriteMc mc, short module, String address, short length ) {

        OperateResultExOne<byte[]> coreResult =
                mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadSmartModule(module, address, length) :
                        mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadSmartModule(module, address, length) : null;
        if (!coreResult.IsSuccess) return coreResult;

        // 核心交互
        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 数据初级解析，需要传入是否使用位的参数
        return OperateResultExOne.CreateSuccessResult(mc.ExtractActualData(read.Content, false));
    }

    /**
     * <b>[商业授权]</b> 读取扩展的数据信息，需要在原有的地址，长度信息之外，输入扩展值信息<br />
     * <b>[Authorization]</b> To read the extended data information, you need to enter the extended value information in addition to the original address and length information
     * @param mc MC通信对象
     * @param extend 扩展信息
     * @param address 地址
     * @param length 数据长度
     * @return 返回结果
     */
    public static OperateResultExOne<byte[]> ReadExtend( IReadWriteMc mc, short extend, String address, short length ) {

        OperateResultExOne<McAddressData> addressResult = mc.McAnalysisAddress(address, length, false);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        byte[] coreResult =
                mc.getMcType() == McType.McBinary ? McBinaryHelper.BuildReadMcCoreExtendCommand(addressResult.Content, extend, false) :
                        mc.getMcType() == McType.MCAscii ? McAsciiHelper.BuildAsciiReadMcCoreExtendCommand(addressResult.Content, extend, false) : null;

        // 核心交互
        OperateResultExOne<byte[]> read = mc.ReadFromCoreServer(coreResult);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(mc.ExtractActualData(read.Content, false));
    }

    /**
     * 远程Run操作<br />
     * Remote Run Operation
     * @param mc MC协议通信对象
     * @return 是否成功
     */
    public static OperateResult RemoteRun( IReadWriteMc mc ) {
        return
                mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{0x01, 0x10, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00}) :
                        mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("1001000000010000".getBytes(StandardCharsets.US_ASCII)) :
                                new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
    }

    /**
     * 远程Stop操作<br />
     * Remote Stop operation
     * @param mc MC协议通信对象
     * @return 是否成功
     */
    public static OperateResult RemoteStop( IReadWriteMc mc ) {
        return
                mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{0x02, 0x10, 0x00, 0x00, 0x01, 0x00}) :
                        mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("100200000001".getBytes(StandardCharsets.US_ASCII)) :
                                new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
    }

    /**
     * 远程Reset操作<br />
     * Remote Reset Operation
     * @param mc MC协议通信对象
     * @return 是否成功
     */
    public static OperateResult RemoteReset( IReadWriteMc mc ) {
        return
                mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{0x06, 0x10, 0x00, 0x00, 0x01, 0x00}) :
                        mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("100600000001".getBytes(StandardCharsets.US_ASCII)) :
                                new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
    }

    /**
     * 读取PLC的型号信息，例如 Q02HCPU<br />
     * Read PLC model information, such as Q02HCPU
     * @param mc MC协议通信对象
     * @return 返回型号的结果对象
     */
    public static OperateResultExOne<String> ReadPlcType( IReadWriteMc mc ) {
        // 核心交互
        OperateResultExOne<byte[]> read =
                mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{0x01, 0x01, 0x00, 0x00}) :
                        mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("01010000".getBytes(StandardCharsets.US_ASCII)) :
                                new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 成功
        return OperateResultExOne.CreateSuccessResult(new String(read.Content, 0, 16, StandardCharsets.US_ASCII).trim());
    }

    /**
     * LED 熄灭 出错代码初始化<br />
     * LED off Error code initialization
     * @param mc MC协议通信对象
     * @return 是否成功
     */
    public static OperateResult ErrorStateReset( IReadWriteMc mc ) {
        return
                mc.getMcType() == McType.McBinary ? mc.ReadFromCoreServer(new byte[]{0x17, 0x16, 0x00, 0x00}) :
                        mc.getMcType() == McType.MCAscii ? mc.ReadFromCoreServer("16170000".getBytes(StandardCharsets.US_ASCII)) :
                                new OperateResult(StringResources.Language.NotSupportedFunction());
    }
}
