package com.iteaj.plugin.protocol.plc.core.address;

import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.profinet.Fuji.FujiSPBHelper;
import com.iteaj.plugin.protocol.plc.core.StringResources;

/**
 * FujiSPB的地址信息，可以携带数据类型，起始地址操作
 */
public class FujiSPBAddress extends DeviceAddressDataBase {

    /**
     * 获取类型代号
     *
     * @return 类型代号信息
     */
    public String getTypeCode() {
        return TypeCode;
    }

    /**
     * 设置类型代号信息
     *
     * @param typeCode 类型代号信息
     */
    public void setTypeCode(String typeCode) {
        TypeCode = typeCode;
    }

    /**
     * 获取位索引信息
     *
     * @return 位索引信息
     */
    public int getBitIndex() {
        return BitIndex;
    }

    /**
     * 设置位索引信息
     *
     * @param bitIndex 位索引信息
     */
    public void setBitIndex(int bitIndex) {
        BitIndex = bitIndex;
    }

    private String TypeCode = "";
    private int BitIndex = 0;

    public String GetWordAddress() {
        return TypeCode + FujiSPBHelper.AnalysisIntegerAddress(getAddressStart());
    }

    /**
     * 获取命令，写入字地址的某一位的命令内容
     *
     * @return 报文信息
     */
    public String GetWriteBoolAddress() {
        int byteIndex = getAddressStart() * 2;
        int bitIndex = BitIndex;
        if (bitIndex >= 8) {
            byteIndex++;
            bitIndex -= 8;
        }

        return TypeCode + FujiSPBHelper.AnalysisIntegerAddress(byteIndex) + String.format("%02X", bitIndex);
    }

    /**
     * 按照位为单位获取相关的索引信息
     *
     * @return 位数据信息
     */
    public int GetBitIndex() {
        return getAddressStart() * 16 + BitIndex;
    }

    // region Static Method

    /**
     * 从实际的Fuji的地址里面解析出地址对象<br />
     * Resolve the address object from the actual Fuji address
     *
     * @param address 富士的地址数据信息
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<FujiSPBAddress> ParseFrom(String address) {
        return ParseFrom(address, (short) 0);
    }

    /**
     * 从实际的Fuji的地址里面解析出地址对象<br />
     * Resolve the address object from the actual Fuji address
     * @param address 富士的地址数据信息
     * @param length 读取的数据长度
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<FujiSPBAddress> ParseFrom(String address, short length) {
        FujiSPBAddress addressData = new FujiSPBAddress();
        try {
            OperateResultExTwo<Integer, String> analysit = PlcHelper.GetBitIndexInformation(address);
            addressData.BitIndex = analysit.Content1;
            address = analysit.Content2;

            switch (address.charAt(0)) {
                case 'X':
                case 'x': {
                    addressData.TypeCode = "01";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'Y':
                case 'y': {
                    addressData.TypeCode = "00";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'M':
                case 'm': {
                    addressData.TypeCode = "02";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'L':
                case 'l': {
                    addressData.TypeCode = "03";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'T':
                case 't': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.TypeCode = "0A";
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    } else if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.TypeCode = "04";
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    } else {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                }
                case 'C':
                case 'c': {
                    if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        addressData.TypeCode = "0B";
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    } else if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        addressData.TypeCode = "05";
                        addressData.setAddressStart(Integer.parseInt(address.substring(2), 10));
                        break;
                    } else {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                }
                case 'D':
                case 'd': {
                    addressData.TypeCode = "0C";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'R':
                case 'r': {
                    addressData.TypeCode = "0D";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'W':
                case 'w': {
                    addressData.TypeCode = "0E";
                    addressData.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                default:
                    throw new Exception(StringResources.Language.NotSupportedDataType());
            }
        } catch (Exception ex) {
            return new OperateResultExOne<FujiSPBAddress>(ex.getMessage());
        }
        return OperateResultExOne.CreateSuccessResult(addressData);
    }

    // endregion
}
