package com.iteaj.plugin.protocol.plc.core.profinet.Delta.Helper;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.modbus.ModbusInfo;
import com.iteaj.plugin.protocol.plc.core.StringResources;

public class DeltaDvpHelper {

    /**
     * 根据台达PLC的地址，解析出转换后的modbus协议信息，适用DVP系列，当前的地址仍然支持站号指定，例如s=2;D100<br />
     * According to the address of Delta PLC, the converted modbus protocol information is parsed out, applicable to DVP series,
     * the current address still supports station number designation, such as s=2;D100
     * @param address 台达plc的地址信息
     * @param modbusCode 原始的对应的modbus信息
     * @return 还原后的modbus地址
     */
    public static OperateResultExOne<String> ParseDeltaDvpAddress(String address, byte modbusCode ) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = stationPara.Content2;
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if (address.startsWith("S") || address.startsWith("s"))
                    return OperateResultExOne.CreateSuccessResult(station + Integer.parseInt(address.substring(1)));
                else if (address.startsWith("X") || address.startsWith("x"))
                    return OperateResultExOne.CreateSuccessResult(station + "x=2;" + (Integer.parseInt(address.substring(1), 8) + 0x400));
                else if (address.startsWith("Y") || address.startsWith("y"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1), 8) + 0x500));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x600));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xE00));
                else if (address.startsWith("M") || address.startsWith("m")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 1536)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 1536 + 0xB000));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + (add + 0x800));
                }
            } else {
                if (address.startsWith("D") || address.startsWith("d")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 4096)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 4096 + 0x9000));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + (add + 0x1000));
                } else if (address.startsWith("C") || address.startsWith("c")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 200)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 200 + 0x0EC8));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + (add + 0x0E00));
                } else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x600));
            }

            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    /**
     * 读取台达PLC的bool变量，重写了读M地址时，跨区域读1536地址时，将会分割多次读取
     * @param readBoolFunc 底层基础的读取方法
     * @param address PLC的地址信息
     * @param length 读取的长度信息
     * @return 读取的结果
     */
    public static OperateResultExOne<boolean[]> ReadBool(FunctionOperateExTwo<String,Short, OperateResultExOne<boolean[]>> readBoolFunc, String address, short length ) {
        String station = "";
        OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
        if (stationPara.IsSuccess) {
            station = "s=" + stationPara.Content1.toString() + ";";
            address = stationPara.Content2;
        }

        if (address.startsWith("M")) {
            try {
                int add = Integer.parseInt(address.substring(1));
                if (add < 1536 && (add + length > 1536)) {
                    // 跨区域读取了，这时候要进行数据切割
                    short len1 = (short) (1536 - add);
                    short len2 = (short) (length - len1);
                    OperateResultExOne<boolean[]> read1 = readBoolFunc.Action(station + address, len1);
                    if (!read1.IsSuccess) return read1;

                    OperateResultExOne<boolean[]> read2 = readBoolFunc.Action(station + "M1536", len2);
                    if (!read2.IsSuccess) return read2;

                    return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceTwoBoolArray(read1.Content, read2.Content));
                }
            } catch (Exception ex) {

            }
        }
        return readBoolFunc.Action(address, length);
    }

    /**
     * 写入台达PLC的bool数据，当发现是M类型的数据，并且地址出现跨1536时，进行切割写入操作
     * @param writeBoolFunc 底层的写入操作方法
     * @param address PLC的起始地址信息
     * @param value 等待写入的数据信息
     * @return 是否写入成功
     */
    public static OperateResult Write(FunctionOperateExTwo<String, boolean[], OperateResult> writeBoolFunc, String address, boolean[] value ) {
        String station = "";
        OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
        if (stationPara.IsSuccess) {
            station = "s=" + stationPara.Content1.toString() + ";";
            address = stationPara.Content2;
        }

        if (address.startsWith("M")) {
            try {
                int add = Integer.parseInt(address.substring(1));
                if (add < 1536 && (add + value.length) > 1536) {
                    // 跨区域写入了，这时候要进行数据切割
                    short len1 = (short) (1536 - add);
                    OperateResult write1 = writeBoolFunc.Action(station + address, SoftBasic.BoolArraySelectMiddle(value, 0, len1));
                    if (!write1.IsSuccess) return write1;

                    OperateResult write2 = writeBoolFunc.Action(station + "M1536", SoftBasic.BoolArraySelectMiddle(value, len1, value.length - len1));
                    if (!write2.IsSuccess) return write2;

                    return OperateResult.CreateSuccessResult();
                }
            } catch (Exception ex) {

            }
        }
        return writeBoolFunc.Action(address, value);
    }

    /**
     * 读取台达PLC的原始字节变量，重写了读D地址时，跨区域读4096地址时，将会分割多次读取
     * @param readFunc 底层基础的读取方法
     * @param address PLC的地址信息
     * @param length 读取的长度信息
     * @return 读取的结果
     */
    public static OperateResultExOne<byte[]> Read( FunctionOperateExTwo<String,Short, OperateResultExOne<byte[]>> readFunc, String address, short length ) {
        String station = "";
        OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
        if (stationPara.IsSuccess) {
            station = "s=" + stationPara.Content1.toString() + ";";
            address = stationPara.Content2;
        }

        if (address.startsWith("D")) {
            try {
                int add = Integer.parseInt(address.substring(1));
                if (add < 4096 && (add + length > 4096)) {
                    // 跨区域读取了，这时候要进行数据切割
                    short len1 = (short) (4096 - add);
                    short len2 = (short) (length - len1);
                    OperateResultExOne<byte[]> read1 = readFunc.Action(station + address, len1);
                    if (!read1.IsSuccess) return read1;

                    OperateResultExOne<byte[]> read2 = readFunc.Action(station + "D4096", len2);
                    if (!read2.IsSuccess) return read2;

                    return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceTwoByteArray(read1.Content, read2.Content));
                }
            } catch (Exception ex) {

            }
        }
        return readFunc.Action(address, length);
    }

    /**
     * 写入台达PLC的原始字节数据，当发现是D类型的数据，并且地址出现跨4096时，进行切割写入操作
     * @param writeFunc 底层的写入操作方法
     * @param address PLC的起始地址信息
     * @param value 等待写入的数据信息
     * @return 是否写入成功
     */
    public static OperateResult Write( FunctionOperateExTwo<String, byte[], OperateResult> writeFunc, String address, byte[] value ) {
        String station = "";
        OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
        if (stationPara.IsSuccess) {
            station = "s=" + stationPara.Content1.toString() + ";";
            address = stationPara.Content2;
        }

        if (address.startsWith("D")) {
            try {
                int add = Integer.parseInt(address.substring(1));
                if (add < 4096 && (add + (value.length / 2)) > 4096) {
                    // 跨区域写入了，这时候要进行数据切割
                    short len1 = (short) (4096 - add);
                    OperateResult write1 = writeFunc.Action(station + address, SoftBasic.BytesArraySelectBegin(value, len1 * 2));
                    if (!write1.IsSuccess) return write1;

                    OperateResult write2 = writeFunc.Action(station + "D4096", SoftBasic.BytesArrayRemoveBegin(value, len1 * 2));
                    if (!write2.IsSuccess) return write2;

                    return OperateResult.CreateSuccessResult();
                }
            } catch (Exception ex) {

            }
        }
        return writeFunc.Action(address, value);
    }
}
