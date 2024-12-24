package com.iteaj.plugin.protocol.plc.core.profinet.Inovance;

import com.iteaj.plugin.protocol.plc.core.modbus.IModbus;
import com.iteaj.plugin.protocol.plc.core.modbus.ModbusInfo;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.types.Convert;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;

/**
 * 汇川PLC的辅助类，提供一些地址解析的方法<br />
 * Auxiliary class of Yaskawa robot, providing some methods of address resolution
 */
public class InovanceHelper {

    // region Static Helper

    private static int CalculateStartAddress( String address ) {
        if (address.indexOf('.') < 0)
            return Integer.parseInt(address);
        else {
            String[] splits = address.split("\\.");
            return Integer.parseInt(splits[0]) * 8 + Integer.parseInt(splits[1]);
        }
    }

    /**
     * 根据汇川PLC的地址，解析出转换后的modbus协议信息，适用AM,H3U,H5U系列的PLC<br />
     * According to the address of Inovance PLC, analyze the converted modbus protocol information, which is suitable for AM, H3U, H5U series PLC
     * @param series PLC的系列
     * @param address 汇川plc的地址信息
     * @param modbusCode 原始的对应的modbus信息
     * @return Modbus格式的地址
     */
    public static OperateResultExOne<String> PraseInovanceAddress(InovanceSeries series, String address, byte modbusCode ) {
        if (series == InovanceSeries.AM) return PraseInovanceAMAddress(address, modbusCode);
        else if (series == InovanceSeries.H3U) return PraseInovanceH3UAddress(address, modbusCode);
        else if (series == InovanceSeries.H5U) return PraseInovanceH5UAddress(address, modbusCode);
        else return new OperateResultExOne<String>("[" + series + "] Not supported series of plc");
    }

    public static OperateResultExOne<String> PraseInovanceAMAddress( String address, byte modbusCode ) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = stationPara.Content2;
            }

            if (address.startsWith("QX") || address.startsWith("qx"))
                return OperateResultExOne.CreateSuccessResult(station + CalculateStartAddress(address.substring(2)));
            else if (address.startsWith("Q") || address.startsWith("q"))
                return OperateResultExOne.CreateSuccessResult(station + CalculateStartAddress(address.substring(1)));
            else if (address.startsWith("IX") || address.startsWith("ix"))
                return OperateResultExOne.CreateSuccessResult(station + "x=2;" + CalculateStartAddress(address.substring(2)));
            else if (address.startsWith("I") || address.startsWith("i"))
                return OperateResultExOne.CreateSuccessResult(station + "x=2;" + CalculateStartAddress(address.substring(1)));
            else if (address.startsWith("MW") || address.startsWith("mw"))
                return OperateResultExOne.CreateSuccessResult(station + address.substring(2));
            else if (address.startsWith( "MD" ) || address.startsWith( "md" )) {
                int add = Integer.parseInt(address.substring(2)) * 2;
                return OperateResultExOne.CreateSuccessResult(station + String.valueOf(add));
            }
            else if (address.startsWith( "MX" ) || address.startsWith( "mx" ))
            {
                if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil)
                {
                    if (address.indexOf( '.' ) > 0)
                    {
                        String[] splits = address.substring( 2 ).split("\\.");
                        int add = Integer.parseInt( splits[0] );
                        int bit = Integer.parseInt( splits[1] );
                        return OperateResultExOne.CreateSuccessResult( station + (add / 2) + "." + (add % 2 * 8 + bit) );
                    }
                    else
                    {
                        int add = Integer.parseInt( address.substring( 2 ) );
                        return OperateResultExOne.CreateSuccessResult( station + (add / 2) + ".0" );
                    }
                }
                else
                {
                    int add = Integer.parseInt( address.substring( 2 ) );
                    return OperateResultExOne.CreateSuccessResult( station + (add / 2) );
                }
            }
            else if (address.startsWith("M") || address.startsWith("m"))
                return OperateResultExOne.CreateSuccessResult(station + address.substring(1));
            else {
                if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                    if (address.startsWith("SMX") || address.startsWith("smx"))
                        return OperateResultExOne.CreateSuccessResult(station + "x=" + (modbusCode + 0x30) + ";" + CalculateStartAddress(address.substring(3)));
                    else if (address.startsWith("SM") || address.startsWith("sm"))
                        return OperateResultExOne.CreateSuccessResult(station + "x=" + (modbusCode + 0x30) + ";" + CalculateStartAddress(address.substring(2)));
                } else {
                    if (address.startsWith("SDW") || address.startsWith("sdw"))
                        return OperateResultExOne.CreateSuccessResult(station + "x=" + (modbusCode + 0x30) + ";" + address.substring(3));
                    else if (address.startsWith("SD") || address.startsWith("sd"))
                        return OperateResultExOne.CreateSuccessResult(station + "x=" + (modbusCode + 0x30) + ";" + address.substring(2));
                }
            }

            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    private static int CalculateH3UStartAddress( String address ) {
        if (address.indexOf('.') < 0)
            return Integer.parseInt(address, 8);
        else {
            String[] splits = address.split("\\.");
            return Integer.parseInt(splits[0], 8) * 8 + Integer.parseInt(splits[1]);
        }
    }

    public static OperateResultExOne<String> PraseInovanceH3UAddress( String address, byte modbusCode ) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = stationPara.Content2;
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if (address.startsWith("X") || address.startsWith("x"))
                    return OperateResultExOne.CreateSuccessResult(station + (CalculateH3UStartAddress(address.substring(1)) + 0xF800));
                else if (address.startsWith("Y") || address.startsWith("y"))
                    return OperateResultExOne.CreateSuccessResult(station + (CalculateH3UStartAddress(address.substring(1)) + 0xFC00));
                else if (address.startsWith("SM") || address.startsWith("sm"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0x2400));
                else if (address.startsWith("S") || address.startsWith("s"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xE000));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xF000));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xF400));
                else if (address.startsWith("M") || address.startsWith("m")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 8000)
                        return OperateResultExOne.CreateSuccessResult(station + (add - 8000 + 0x1F40));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + add);
                }
            } else {
                if (address.startsWith("D") || address.startsWith("d"))
                    return OperateResultExOne.CreateSuccessResult(station + Integer.parseInt(address.substring(1)));
                else if (address.startsWith("SD") || address.startsWith("sd"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0x2400));
                else if (address.startsWith("R") || address.startsWith("r"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x3000));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xF000));
                else if (address.startsWith("C") || address.startsWith("c")) {
                    int add = Integer.parseInt(address.substring(1));
                    if (add >= 200)
                        return OperateResultExOne.CreateSuccessResult(station + ((add - 200) * 2 + 0xF700));
                    else
                        return OperateResultExOne.CreateSuccessResult(station + (add + 0xF400));
                }
            }

            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    public static OperateResultExOne<String> PraseInovanceH5UAddress( String address, byte modbusCode ) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1 + ";";
                address = stationPara.Content2;
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if (address.startsWith("X") || address.startsWith("x"))
                    return OperateResultExOne.CreateSuccessResult(station + (CalculateH3UStartAddress(address.substring(1)) + 0xF800));
                else if (address.startsWith("Y") || address.startsWith("y"))
                    return OperateResultExOne.CreateSuccessResult(station + (CalculateH3UStartAddress(address.substring(1)) + 0xFC00));
                else if (address.startsWith("S") || address.startsWith("s"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xE000));
                else if (address.startsWith("B") || address.startsWith("b"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x3000));
                else if (address.startsWith("M") || address.startsWith("m"))
                    return OperateResultExOne.CreateSuccessResult(station + Integer.parseInt(address.substring(1)));
            } else {
                if (address.startsWith("D") || address.startsWith("d"))
                    return OperateResultExOne.CreateSuccessResult(station + Integer.parseInt(address.substring(1)));
                else if (address.startsWith("R") || address.startsWith("r"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x3000));
            }

            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    /**
     * 按照字节读取汇川M地址的数据，地址示例： MB100，MB101，需要注意的是，MB100 及 MB101 的地址是 MW50 的数据。<br />
     * Read the data of Inovance M address according to the byte, address example: MB100, MB101, it should be noted that the addresses of MB100 and MB101 are the data of MW50.
     * @param modbus 汇川的PLC对象
     * @param address 地址信息
     * @return 读取的结果数据
     */
    public static OperateResultExOne<Byte> ReadByte(IModbus modbus, String address ) {
        try {
            int offset = 0;
            if (address.startsWith("MB") || address.startsWith("mb")) offset = Convert.ToInt32(address.substring(2));
            else if (address.startsWith("M") || address.startsWith("m")) offset = Convert.ToInt32(address.substring(1));
            else new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());

            OperateResultExOne<byte[]> read = modbus.Read("MW" + String.valueOf(offset / 2), (short) 1);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            return OperateResultExOne.CreateSuccessResult(offset % 2 == 0 ? read.Content[1] : read.Content[0]);
        } catch (Exception ex) {
            return new OperateResultExOne<Byte>("Address prase failed: " + ex.getMessage());
        }
    }

    // endregion
}
