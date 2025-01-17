package com.iteaj.plugin.protocol.plc.core.address;

import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;

/**
 * 永宏编程口的地址类对象
 */
public class FatekProgramAddress extends DeviceAddressDataBase {
    /**
     * 获取数据类型代号
     * @return 数据类型代号
     */
    public String getDataCode() {
        return DataCode;
    }

    /**
     * 设置一个新的数据类型代号
     * @param dataCode 数据类型代号
     */
    public void setDataCode(String dataCode) {
        DataCode = dataCode;
    }

    private String DataCode;

    @Override
    public void Parse(String address, int length) {
        OperateResultExOne<FatekProgramAddress> addressData = ParseFrom(address, length);
        if (addressData.IsSuccess) {
            setAddressStart(addressData.Content.getAddressStart());
            setLength(addressData.Content.getLength());
            setDataCode(addressData.Content.getDataCode());
        }
    }

    @Override
    public String toString() {
        if (
                getDataCode().equals("X") ||
                        getDataCode().equals("Y") ||
                        getDataCode().equals("M") ||
                        getDataCode().equals("S") ||
                        getDataCode().equals("T") ||
                        getDataCode().equals("C") ||
                        getDataCode().equals("RT") ||
                        getDataCode().equals("RC")
        ) return getDataCode() + String.format("%04d", getAddressStart());
        else return getDataCode() + String.format("%05d", getAddressStart());
    }

    /**
     * 从普通的PLC的地址转换为HSL标准的地址信息
     * @param address 地址信息
     * @param length 数据长度
     * @return 是否成功的地址结果
     */
    public static OperateResultExOne<FatekProgramAddress> ParseFrom(String address, int length ) {
        try {
            FatekProgramAddress programAddress = new FatekProgramAddress();
            switch (address.charAt(0)) {
                case 'X':
                case 'x': {
                    programAddress.DataCode = "X";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'Y':
                case 'y': {
                    programAddress.DataCode = "Y";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'M':
                case 'm': {
                    programAddress.DataCode = "M";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'S':
                case 's': {
                    programAddress.DataCode = "S";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'T':
                case 't': {
                    programAddress.DataCode = "T";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'C':
                case 'c': {
                    programAddress.DataCode = "C";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'D':
                case 'd': {
                    programAddress.DataCode = "D";
                    programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    break;
                }
                case 'R':
                case 'r': {
                    if (address.charAt(1) == 'T' || address.charAt(1) == 't') {
                        programAddress.DataCode = "RT";
                        programAddress.setAddressStart(Integer.parseInt(address.substring(2), 10));
                    } else if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        programAddress.DataCode = "RC";
                        programAddress.setAddressStart(Integer.parseInt(address.substring(2), 10));
                    } else {
                        programAddress.DataCode = "R";
                        programAddress.setAddressStart(Integer.parseInt(address.substring(1), 10));
                    }
                    break;
                }
                default:
                    throw new Exception(StringResources.Language.NotSupportedDataType());
            }

            return OperateResultExOne.CreateSuccessResult(programAddress);
        } catch (Exception ex) {
            return new OperateResultExOne<FatekProgramAddress>(ex.getMessage());
        }
    }
}
