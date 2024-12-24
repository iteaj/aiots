package com.iteaj.plugin.protocol.plc.core.profinet.Delta.Helper;

import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.modbus.ModbusInfo;
import com.iteaj.plugin.protocol.plc.core.StringResources;

/**
 * 台达AS300的辅助帮助类信息
 */
public class DeltaASHelper {

    private static int ParseDeltaBitAddress(String address) {
        int bitIndex = address.indexOf('.');
        if (bitIndex > 0)
            return Integer.parseInt(address.substring(0, bitIndex)) * 16 + PlcHelper.CalculateBitStartIndex(address.substring(bitIndex + 1));
        else
            return Integer.parseInt(address) * 16;
    }

    /**
     * 根据台达AS300的PLC的地址，解析出转换后的modbus协议信息，适用AS300系列，当前的地址仍然支持站号指定，例如s=2;D100<br />
     * According to the PLC address of Delta AS300, the converted modbus protocol information is parsed,
     * and it is applicable to AS300 series. The current address still supports station number designation, for example, s=2;D100
     * @param address 台达plc的地址信息
     * @param modbusCode 原始的对应的modbus信息
     * @return 还原后的modbus地址
     */
    public static OperateResultExOne<String> ParseDeltaASAddress(String address, byte modbusCode) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                station = "s=" + stationPara.Content1.toString() + ";";
                address = stationPara.Content2;
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if (address.startsWith("SM") || address.startsWith("sm"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0x4000));
                else if (address.startsWith("HC") || address.startsWith("hc"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xFC00));
                else if (address.startsWith("S") || address.startsWith("s"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x5000));
                else if (address.startsWith("X") || address.startsWith("x"))
                    return OperateResultExOne.CreateSuccessResult(station + "x=2;" + (ParseDeltaBitAddress(address.substring(1)) + 0x6000));
                else if (address.startsWith("Y") || address.startsWith("y"))
                    return OperateResultExOne.CreateSuccessResult(station + (ParseDeltaBitAddress(address.substring(1)) + 0xA000));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xE000));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xF000));
                else if (address.startsWith("M") || address.startsWith("m"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x0000));
            } else {
                if (address.startsWith("SR") || address.startsWith("sr"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xC000));
                else if (address.startsWith("HC") || address.startsWith("hc"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(2)) + 0xFC00));
                else if (address.startsWith("D") || address.startsWith("d"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0x0000));
                else if (address.startsWith("X") || address.startsWith("x"))
                    return OperateResultExOne.CreateSuccessResult(station + "x=4;" + (Integer.parseInt(address.substring(1)) + 0x8000));
                else if (address.startsWith("Y") || address.startsWith("y"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xA000));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xF000));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xE000));
                else if (address.startsWith("E") || address.startsWith("e"))
                    return OperateResultExOne.CreateSuccessResult(station + (Integer.parseInt(address.substring(1)) + 0xFE00));
            }

            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }
}
