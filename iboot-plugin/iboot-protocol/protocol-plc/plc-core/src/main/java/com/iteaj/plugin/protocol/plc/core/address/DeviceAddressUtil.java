package com.iteaj.plugin.protocol.plc.core.address;

public class DeviceAddressUtil {

    /**
     * 位地址转换方法，101等同于10.1等同于10*16+1=161
     * Bit address conversion method, 101 is equivalent to 10.1 is equivalent to 10 * 16 + 1 = 161
     *
     * @param address 地址信息
     * @return 实际的位地址信息
     */
    public static int CalculateComplexAddress(String address) {
        int add = 0;
        if (!address.contains(".")) {
            if (address.length() == 1)
                add = Integer.parseInt(address, 16);
            else
                add = Integer.parseInt(address.substring(0, address.length() - 1)) * 16 + Integer.parseInt(address.substring(address.length() - 1), 16);
        } else {
            add = Integer.parseInt(address.substring(0, address.indexOf("."))) * 16;
            String bit = address.substring(address.indexOf(".") + 1);
            if (bit.contains("A") || bit.contains("B") || bit.contains("C") || bit.contains("D") || bit.contains("E") || bit.contains("F")) {
                add += Integer.parseInt(bit, 16);
            } else {
                add += Integer.parseInt(bit);
            }
        }
        return add;
    }
}
