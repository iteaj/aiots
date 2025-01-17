package com.iteaj.plugin.protocol.plc.core.profinet.XINJE;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.address.XinJEAddress;
import com.iteaj.plugin.protocol.plc.core.types.BitConverter;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.modbus.ModbusInfo;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.util.ArrayList;

public class XinJEHelper {


    private static int CalculateXinJEStartAddress(String address) {
        if (address.indexOf('.') < 0)
            return Integer.parseInt(address, 8);
        else {
            String[] splits = address.split("\\.");
            return Integer.parseInt(splits[0], 8) * 8 + Integer.parseInt(splits[1]);
        }
    }

    /**
     * 根据信捷PLC的地址，解析出转换后的modbus协议信息
     *
     * @param series     PLC的系列信息
     * @param address    汇川plc的地址信息
     * @param modbusCode 原始的对应的modbus信息
     * @return 还原后的modbus地址
     */
    public static OperateResultExOne<String> PraseXinJEAddress(XinJESeries series, String address, byte modbusCode) {
        if (series == XinJESeries.XC) return PraseXinJEXCAddress(address, modbusCode);
        return PraseXinJEXD1XD2XD3XL1XL3Address(address, modbusCode);
    }

    /**
     * 根据信捷PLC的地址，解析出转换后的modbus协议信息，适用XC系列
     *
     * @param address    安川plc的地址信息
     * @param modbusCode 原始的对应的modbus信息
     * @return 还原后的modbus地址
     */
    public static OperateResultExOne<String> PraseXinJEXCAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = stationPara.Content2;
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if (address.startsWith("X") || address.startsWith("x"))
                    return OperateResultExOne.CreateSuccessResult(station + (CalculateXinJEStartAddress(address.substring(1)) + 0x4000));
                else if (address.startsWith("Y") || address.startsWith("y"))
                    return OperateResultExOne.CreateSuccessResult(station + (CalculateXinJEStartAddress(address.substring(1)) + 0x4800));
                else if (address.startsWith("S") || address.startsWith("s"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x5000));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x6400));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x6C00));
                else if (address.startsWith("M") || address.startsWith("m")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 8000)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 8000 + 0x6000));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + add);
                }
            } else {
                if (address.startsWith("D") || address.startsWith("d")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 8000)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 8000 + 0x4000));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + add);
                } else if (address.startsWith("F") || address.startsWith("f")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 8000)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 8000 + 0x6800));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + (add + 0x4800));
                } else if (address.startsWith("E") || address.startsWith("e"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x7000));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x3000));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x3800));
            }

            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    /**
     * 解析信捷的XD1,XD2,XD3,XL1,XL3系列的PLC的Modbus地址和内部软元件的对照
     *
     * @param address    PLC内部的软元件的地址
     * @param modbusCode 默认的Modbus功能码
     * @return 解析后的Modbus地址
     */
    public static OperateResultExOne<String> PraseXinJEXD1XD2XD3XL1XL3Address(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = stationPara.Content2;
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if (address.startsWith("X") || address.startsWith("x")) {
                    int start = CalculateXinJEStartAddress(address.substring(1));
                    if (start < 0x1000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 0x0000 + 0x5000)); // X0 - X77
                    if (start < 0x2000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 0x1000 + 0x5100)); // X10000 - X11177  10个模块
                    if (start < 0x3000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 0x2000 + 0x58D0)); // X20000 - X20177  2个模块
                    return OperateResultExOne.CreateSuccessResult(station + (start - 0x3000 + 0x5BF0)); // #1 ED
                } else if (address.startsWith("Y") || address.startsWith("y")) {
                    int start = CalculateXinJEStartAddress(address.substring(1));
                    if (start < 0x1000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 0x0000 + 0x6000)); // Y0 - Y77
                    if (start < 0x2000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 0x1000 + 0x6100)); // Y10000 - Y11177  10个模块
                    if (start < 0x3000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 0x2000 + 0x68D0)); // Y20000 - Y20177  2个模块
                    return OperateResultExOne.CreateSuccessResult(station + (start - 0x3000 + 0x6BF0)); // #1 ED
                } else if (address.startsWith("SEM") || address.startsWith("sem"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 0xC080));
                else if (address.startsWith("HSC") || address.startsWith("hsc"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 0xE900));
                else if (address.startsWith("SM") || address.startsWith("sm"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0x9000));
                else if (address.startsWith("ET") || address.startsWith("et"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xC000));
                else if (address.startsWith("HM") || address.startsWith("hm"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xC100));
                else if (address.startsWith("HS") || address.startsWith("hs"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xD900));
                else if (address.startsWith("HT") || address.startsWith("ht"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xE100));
                else if (address.startsWith("HC") || address.startsWith("hc"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xE500));
                else if (address.startsWith("S") || address.startsWith("s"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x7000));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xA000));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xB000));
                else if (address.startsWith("M") || address.startsWith("m"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x0000));
            } else {
                if (address.startsWith("ID") || address.startsWith("id")) {
                    int start = Integer.parseInt(address.substring(2));
                    if (start < 10000)
                        return OperateResultExOne.CreateSuccessResult(station + (start + 0x5000)); // ID0 - ID99
                    if (start < 20000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 10000 + 0x5100)); // ID10000 - ID10999
                    if (start < 30000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 20000 + 0x58D0)); // ID20000 - ID20199
                    return OperateResultExOne.CreateSuccessResult(station + (start - 30000 + 0x5BF0)); // ID30000 - ID30099
                } else if (address.startsWith("QD") || address.startsWith("qd")) {
                    int start = Integer.parseInt(address.substring(2));
                    if (start < 10000)
                        return OperateResultExOne.CreateSuccessResult(station + (start + 0x6000)); // QD0 - QD99
                    if (start < 20000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 10000 + 0x6100)); // QD10000 - QD10999
                    if (start < 30000)
                        return OperateResultExOne.CreateSuccessResult(station + (start - 20000 + 0x68D0)); // QD20000 - QD20199
                    return OperateResultExOne.CreateSuccessResult(station + (start - 30000 + 0x6BF0)); // QD30000 - QD30099
                } else if (address.startsWith("HSCD") || address.startsWith("hscd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(4)) + 0xC480));
                else if (address.startsWith("ETD") || address.startsWith("etd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 0xA000));
                else if (address.startsWith("HSD") || address.startsWith("hsd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 0xB880));
                else if (address.startsWith("HTD") || address.startsWith("htd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 0xBC80));
                else if (address.startsWith("HCD") || address.startsWith("hcd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 0xC080));
                else if (address.startsWith("SFD") || address.startsWith("sfd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(3)) + 0xE4C0));
                else if (address.startsWith("SD") || address.startsWith("sd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0x7000));
                else if (address.startsWith("TD") || address.startsWith("td"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0x8000));
                else if (address.startsWith("CD") || address.startsWith("cd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0x9000));
                else if (address.startsWith("HD") || address.startsWith("hd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xA080));
                else if (address.startsWith("FD") || address.startsWith("fd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xC4C0));
                else if (address.startsWith("FS") || address.startsWith("fs"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xF4C0));
                else if (address.startsWith("D") || address.startsWith("d"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x0000));
            }

            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }



    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(byte station, String address, short length, boolean isBit) {
        OperateResultExOne<XinJEAddress> read = XinJEAddress.ParseFrom(address, station);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return BuildReadCommand(read.Content, length, isBit);
    }

    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(XinJEAddress address, short length, boolean isBit) {
        ArrayList<byte[]> array = new ArrayList<>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, isBit ? 120 * 16 : 120);
        for (int i = 0; i < splits.length; i++) {
            byte[] command = new byte[8];
            command[0] = address.Station;
            command[1] = isBit ? (byte) 0x1E : (byte) 0x20;
            command[2] = address.DataCode;
            command[3] = BitConverter.GetBytes(address.getAddressStart())[2];
            command[4] = BitConverter.GetBytes(address.getAddressStart())[1];
            command[5] = BitConverter.GetBytes(address.getAddressStart())[0];
            command[6] = BitConverter.GetBytes(splits[i])[1];
            command[7] = BitConverter.GetBytes(splits[i])[0];

            address.setAddressStart( address.getAddressStart() + splits[i] );
            array.add(command);
        }
        return OperateResultExOne.CreateSuccessResult(array);
    }

    public static OperateResultExOne<byte[]> BuildWriteWordCommand(byte station, String address, byte[] value) {
        OperateResultExOne<XinJEAddress> read = XinJEAddress.ParseFrom(address, station);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return BuildWriteWordCommand(read.Content, value);
    }


    public static OperateResultExOne<byte[]> BuildWriteWordCommand(XinJEAddress address, byte[] value) {
        byte[] command = new byte[9 + value.length];
        command[0] = address.Station;
        command[1] = 0x21;
        command[2] = address.DataCode;
        command[3] = BitConverter.GetBytes(address.getAddressStart())[2];
        command[4] = BitConverter.GetBytes(address.getAddressStart())[1];
        command[5] = BitConverter.GetBytes(address.getAddressStart())[0];
        command[6] = BitConverter.GetBytes(value.length / 2)[1];
        command[7] = BitConverter.GetBytes(value.length / 2)[0];
        command[8] = (byte) value.length;
        Utilities.ByteArrayCopyTo(value, command, 9);
        return OperateResultExOne.CreateSuccessResult(command);
    }

    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(byte station, String address, boolean[] value) {
        OperateResultExOne<XinJEAddress> read = XinJEAddress.ParseFrom(address, station);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return BuildWriteBoolCommand(read.Content, value);
    }


    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(XinJEAddress address, boolean[] value) {
        byte[] buffer = SoftBasic.BoolArrayToByte(value);
        byte[] command = new byte[9 + buffer.length];
        command[0] = address.Station;
        command[1] = 0x1F;
        command[2] = address.DataCode;
        command[3] = BitConverter.GetBytes(address.getAddressStart())[2];
        command[4] = BitConverter.GetBytes(address.getAddressStart())[1];
        command[5] = BitConverter.GetBytes(address.getAddressStart())[0];
        command[6] = BitConverter.GetBytes(value.length)[1];
        command[7] = BitConverter.GetBytes(value.length)[0];
        command[8] = (byte) buffer.length;
        Utilities.ByteArrayCopyTo(buffer, command, 9);
        return OperateResultExOne.CreateSuccessResult(command);
    }

}
