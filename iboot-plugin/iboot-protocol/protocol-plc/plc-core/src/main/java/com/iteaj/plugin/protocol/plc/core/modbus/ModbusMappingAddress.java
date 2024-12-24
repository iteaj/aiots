package com.iteaj.plugin.protocol.plc.core.modbus;

import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.address.DeviceAddressDataBase;
import com.iteaj.plugin.protocol.plc.core.types.*;

/**
 * Modbus的地址映射类
 */
public class ModbusMappingAddress {

    private static int ParseBitAddress( String address )
    {
        return ParseBitAddress(address, 16);
    }
    private static int ParseBitAddress( String address, int wordLength ) {
        int bitIndex = address.indexOf('.');
        if (bitIndex > 0)
            return Convert.ToInt32(address.substring(0, bitIndex)) * wordLength + PlcHelper.CalculateBitStartIndex(address.substring(bitIndex + 1));
        else
            return Convert.ToInt32(address) * wordLength;
    }


    /**
     * 根据台达AS300的PLC的地址，解析出转换后的modbus协议信息，适用AS300系列，当前的地址仍然支持站号指定，例如s=2;D100<br />
     * According to the PLC address of Delta AS300, the converted modbus protocol information is parsed,
     * and it is applicable to AS300 series. The current address still supports station number designation, for example, s=2;D100
     * @param address 台达plc的地址信息
     * @param modbusCode 原始的对应的modbus信息
     * @return 还原后的modbus地址
     */
    public static OperateResultExOne<String> Delta_AS(String address, byte modbusCode ) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                address = stationPara.Content2;
                station = "s=" + stationPara.Content1 + ";";
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if (address.startsWith("SM") || address.startsWith("sm"))
                    return OperateResultExOne.CreateSuccessResult(station + String.valueOf(Convert.ToInt32(address.substring(2)) + 0x4000));
                else if (address.startsWith("HC") || address.startsWith("hc"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 0xFC00));
                else if (address.startsWith("S") || address.startsWith("s"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x5000));
                else if (address.startsWith("X") || address.startsWith("x"))
                    return OperateResultExOne.CreateSuccessResult(station + "x=2;" + (ParseBitAddress(address.substring(1)) + 0x6000));
                else if (address.startsWith("Y") || address.startsWith("y"))
                    return OperateResultExOne.CreateSuccessResult(station + (ParseBitAddress(address.substring(1)) + 0xA000));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0xE000));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0xF000));
                else if (address.startsWith("M") || address.startsWith("m"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x0000));
                else if (address.startsWith("D") && address.indexOf(".") > 0)
                    return OperateResultExOne.CreateSuccessResult(station + address);
            } else {
                if (address.startsWith("SR") || address.startsWith("sr"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 0xC000));
                else if (address.startsWith("HC") || address.startsWith("hc"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 0xFC00));
                else if (address.startsWith("D") || address.startsWith("d"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x0000));
                else if (address.startsWith("X") || address.startsWith("x"))
                    return OperateResultExOne.CreateSuccessResult(station + "x=4;" + (Convert.ToInt32(address.substring(1)) + 0x8000));
                else if (address.startsWith("Y") || address.startsWith("y"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0xA000));
                else if (address.startsWith("C") || address.startsWith("c"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0xF000));
                else if (address.startsWith("T") || address.startsWith("t"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0xE000));
                else if (address.startsWith("E") || address.startsWith("e"))
                    return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0xFE00));
            }

            return new OperateResultExOne<String>(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage());
        }
    }

    /**
     * 维控的Lx5v的Modbus地址转换方法，主要注入到Modbus类，即可以在Modbus里使用维控的地址进行通信
     * @param address 维控plc的地址信息
     * @param modbusCode 原始的对应的modbus信息
     * @return 还原后的modbus地址
     */
    public static OperateResultExOne<String> WeCon_Lx5v( String address, byte modbusCode ) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                address = stationPara.Content2;
                station = "s=" + stationPara.Content1 + ";";
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if      (address.matches("^T[0-9]+"))   return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x0000));
                else if (address.matches("^C[0-9]+"))   return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x0600));
                else if (address.matches("^LC[0-9]+"))  return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 0x0A00));
                else if (address.matches("^HSC[0-9]+")) return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(3)) + 0x0E00));
                else if (address.matches("^M[0-9]+"))   return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x1000));
                else if (address.matches("^SM[0-9]+"))  return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 0x5000));
                else if (address.matches("^S[0-9]+"))   return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0xC000));
                else if (address.matches("^X[0-9]+"))   return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1), 8) + 0xE000));
                else if (address.matches("^Y[0-9]+"))   return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1), 8) + 0xF000));

                //if (address.IndexOf( '.' ) > 0) return OperateResult.CreateSuccessResult( station + address );  // 如果包含了小数点，就直接返回当前的信息
                return OperateResultExOne.CreateSuccessResult(ModbusHelper.TransPointAddressToModbus(station, address,
                        new String[]{"D", "SD", "R"},
                        new int[]{0x1000, 0x5000, 0x8000},
                        new FunctionOperateExOne<String, Integer>() {
                            @Override
                            public Integer Action(String content) {
                                return Integer.parseInt(content);
                            }
                        }));
            } else {
                if (address.matches("^T[0-9]+")) return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x0000));
                else if (address.matches("^C[0-9]+")) return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x0600));
                else if (address.matches("^LC[0-9]+")) return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) * 2 + 0x0A00));   // 双字的地址
                else if (address.matches("^HSC[0-9]+")) return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(3)) * 2 + 0x0E00));
                else if (address.matches("^D[0-9]+")) return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x1000));
                else if (address.matches("^SD[0-9]+")) return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(2)) + 0x5000));
                else if (address.matches("^R[0-9]+")) return OperateResultExOne.CreateSuccessResult(station + (Convert.ToInt32(address.substring(1)) + 0x8000));
            }

            throw new Exception(StringResources.Language.NotSupportedDataType());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(DeviceAddressDataBase.GetUnsupportedAddressInfo(address, ex));
        }
    }

    /**
     * 英威腾 TS600 的modbus地址变换
     * @param address 英威腾PLC的地址
     * @param modbusCode 默认的modbus功能码
     * @return 还原后的modbus地址
     */
    public static OperateResultExOne<String> Invt_Ts( String address, byte modbusCode ) {
        try {
            String station = "";
            OperateResultExTwo<Integer, String> stationPara = PlcHelper.ExtractParameter(address, "s");
            if (stationPara.IsSuccess) {
                address = stationPara.Content2;
                station = "s=" + stationPara.Content1 + ";";
            }

            if (modbusCode == ModbusInfo.ReadCoil || modbusCode == ModbusInfo.WriteCoil || modbusCode == ModbusInfo.WriteOneCoil) {
                if      (address.matches("^M[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + Convert.ToInt32( address.substring( 1 ) ) );
                else if (address.matches("^S[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + (Convert.ToInt32( address.substring( 1 ) ) + 0x8000) );
                else if (address.matches("^X[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + (Convert.ToInt32( address.substring( 1 ), 8 ) + 0xA000) );
                else if (address.matches("^Y[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + (Convert.ToInt32( address.substring( 1 ), 8 ) + 0xB000) );
                else if (address.matches("^T[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + (Convert.ToInt32( address.substring( 1 ) ) + 0xC000) );
                else if (address.matches("^C[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + (Convert.ToInt32( address.substring( 1 ) ) + 0xC200) );

                //if (address.IndexOf( '.' ) > 0) return OperateResult.CreateSuccessResult( station + address );  // 如果包含了小数点，就直接返回当前的信息
                return  OperateResultExOne.CreateSuccessResult(ModbusHelper.TransPointAddressToModbus( station, address,
                        new String[] { "D", "R" },
                        new int[] { 0x0000, 0x8000 },
                        new FunctionOperateExOne<String, Integer>() {
                            @Override
                            public Integer Action(String content) {
                                return Integer.parseInt(content);
                            }
                        }));

            } else {
                if      (address.matches("^T[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + (Convert.ToInt32( address.substring( 1 ) ) + 0xE000) );
                else if (address.matches("^C[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + (Convert.ToInt32( address.substring( 1 ) ) + 0xE200) );
                else if (address.matches("^Z[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + (Convert.ToInt32( address.substring( 2 ) ) * 2 + 0xE400) );
                else if (address.matches("^D[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + Convert.ToInt32( address.substring( 1 ) ) );
                else if (address.matches("^R[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + (Convert.ToInt32( address.substring( 1 ) ) + 0x8000) );
                else if (address.matches("^M[0-9]+" )) return OperateResultExOne.CreateSuccessResult( station + "x=1;" + Convert.ToInt32( address.substring( 1 ) ) );
            }

            throw new Exception( StringResources.Language.NotSupportedDataType() );
        }
        catch (Exception ex) {
            return new OperateResultExOne<String>(DeviceAddressDataBase.GetUnsupportedAddressInfo( address, ex ) );
        }
    }
}
