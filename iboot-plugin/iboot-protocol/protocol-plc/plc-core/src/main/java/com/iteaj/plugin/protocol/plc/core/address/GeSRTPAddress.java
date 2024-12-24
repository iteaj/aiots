package com.iteaj.plugin.protocol.plc.core.address;

import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;

public class GeSRTPAddress extends DeviceAddressDataBase {

    /**
     * 获取等待读取的数据的代码
     * Get the code of the data waiting to be read
     *
     * @return code
     */
    public byte getDataCode() {
        return DataCode;
    }

    /**
     * 设置等待读取的数据的代码
     * Set the code of the data waiting to be read
     *
     * @param dataCode 数据代码
     */
    public void setDataCode(byte dataCode) {
        DataCode = dataCode;
    }

    private byte DataCode = 0;


    @Override
    public void Parse(String address, int length) {
        super.Parse(address, length);
    }


    public static OperateResultExOne<GeSRTPAddress> ParseFrom(String address, boolean isBit) {
        return ParseFrom( address, (short) 0, isBit );
    }

    /**
     * 从GE的地址里，解析出实际的带数据码的 {@link GeSRTPAddress} 地址信息，起始地址会自动减一，和实际的地址相匹配
     *
     * @param address 实际的地址数据
     * @param length  读取的长度信息
     * @param isBit   是否位操作
     * @return 是否成功的GE地址对象
     */
    public static OperateResultExOne<GeSRTPAddress> ParseFrom(String address, short length, boolean isBit) {
        GeSRTPAddress addressData = new GeSRTPAddress();
        try {
            addressData.setLength(length);
            if (address.startsWith("AI") || address.startsWith("ai")) {
                if (isBit)
                    return new OperateResultExOne<GeSRTPAddress>(StringResources.Language.GeSRTPNotSupportBitReadWrite());
                addressData.DataCode = 0x0A;
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else if (address.startsWith("AQ") || address.startsWith("aq")) {
                if (isBit)
                    return new OperateResultExOne<GeSRTPAddress>(StringResources.Language.GeSRTPNotSupportBitReadWrite());
                addressData.DataCode = 0x0C;
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else if (address.startsWith("R") || address.startsWith("r")) {
                if (isBit)
                    return new OperateResultExOne<GeSRTPAddress>(StringResources.Language.GeSRTPNotSupportBitReadWrite());
                addressData.DataCode = 0x08;
                addressData.setAddressStart(Integer.parseInt(address.substring(1)));
            } else if (address.startsWith("SA") || address.startsWith("sa")) {
                addressData.DataCode = isBit ? (byte) 0x4E : (byte) 0x18;
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else if (address.startsWith("SB") || address.startsWith("sb")) {
                addressData.DataCode = isBit ? (byte) 0x50 : (byte) 0x1A;
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else if (address.startsWith("SC") || address.startsWith("sc")) {
                addressData.DataCode = isBit ? (byte) 0x52 : (byte) 0x1C;
                addressData.setAddressStart(Integer.parseInt(address.substring(2)));
            } else {
                if (address.charAt(0) == 'I' || address.charAt(0) == 'i')
                    addressData.DataCode = isBit ? (byte) 0x46 : (byte) 0x10;
                else if (address.charAt(0) == 'Q' || address.charAt(0) == 'q')
                    addressData.DataCode = isBit ? (byte) 0x48 : (byte) 0x12;
                else if (address.charAt(0) == 'M' || address.charAt(0) == 'm')
                    addressData.DataCode = isBit ? (byte) 0x4C : (byte) 0x16;
                else if (address.charAt(0) == 'T' || address.charAt(0) == 't')
                    addressData.DataCode = isBit ? (byte) 0x4A : (byte) 0x14;
                else if (address.charAt(0) == 'S' || address.charAt(0) == 's')
                    addressData.DataCode = isBit ? (byte) 0x54 : (byte) 0x1E;
                else if (address.charAt(0) == 'G' || address.charAt(0) == 'g')
                    addressData.DataCode = isBit ? (byte) 0x56 : (byte) 0x38;
                else throw new Exception(StringResources.Language.NotSupportedDataType());

                addressData.setAddressStart(Integer.parseInt(address.substring(1)));
            }
        } catch (Exception ex) {
            return new OperateResultExOne<>(ex.getMessage());
        }

        if (addressData.getAddressStart() == 0)
            return new OperateResultExOne<GeSRTPAddress>(StringResources.Language.GeSRTPAddressCannotBeZero());
        if (addressData.getAddressStart() > 0) addressData.setAddressStart(addressData.getAddressStart() - 1);
        return OperateResultExOne.CreateSuccessResult(addressData);
    }

    // endregion
}
