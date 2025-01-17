package com.iteaj.plugin.protocol.plc.core.address;

import com.iteaj.plugin.protocol.plc.core.types.Convert;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富士CommandSettingsType的地址类信息
 */
public class FujiCommandSettingTypeAddress extends DeviceAddressDataBase {

    /**
     * 数据的代号信息
     */
    public byte DataCode = 0x00;

    /**
     * 地址的头信息，缓存的情况
     */
    public String AddressHeader = "";

    public void Parse( String address, int length ) {
        super.Parse(address, length);
    }

    public String toString( ) {
        return AddressHeader + getAddressStart();
    }

    /**
     * 从字符串地址解析fuji的实际地址信息，如果解析成功，则 OperateResultExOne.IsSuccess 为 True，取 OperateResultExOne.Content 值即可。
     * @param address 字符串地址
     * @param length 读取的长度信息
     * @return 是否解析成功
     */
    public static OperateResultExOne<FujiCommandSettingTypeAddress> ParseFrom(String address, int length ) {
        try {
            FujiCommandSettingTypeAddress fujiAddress = new FujiCommandSettingTypeAddress();
            String addType = "";
            String addOffset = "";
            if (address.indexOf('.') < 0) {
                Matcher matcher = Pattern.compile("^[A-Z]+").matcher(address);
                if (!matcher.find()) {
                    return new OperateResultExOne<FujiCommandSettingTypeAddress>(StringResources.Language.NotSupportedDataType());
                }

                addType = matcher.group();
                addOffset = address.substring(addType.length());
            } else {
                String[] splits = Utilities.SplitDot(address);
                if (splits[0].charAt(0) != 'W')
                    return new OperateResultExOne<FujiCommandSettingTypeAddress>(StringResources.Language.NotSupportedDataType());

                addType = splits[0];
                addOffset = splits[1];
            }
            fujiAddress.AddressHeader = addType;
            fujiAddress.setAddressStart(Convert.ToInt32(addOffset));
            fujiAddress.setLength(length);

            if (addType.equals("TS")) fujiAddress.DataCode = 0x0A;
            else if (addType.equals("TR")) fujiAddress.DataCode = 0x0B;
            else if (addType.equals("CS")) fujiAddress.DataCode = 0x0C;
            else if (addType.equals("CR")) fujiAddress.DataCode = 0x0D;
            else if (addType.equals("BD")) fujiAddress.DataCode = 0x0E;
            else if (addType.equals("WL")) fujiAddress.DataCode = 0x14;
            else if (addType.equals("B")) fujiAddress.DataCode = 0x00;
            else if (addType.equals("M")) fujiAddress.DataCode = 0x01;
            else if (addType.equals("K")) fujiAddress.DataCode = 0x02;
            else if (addType.equals("F")) fujiAddress.DataCode = 0x03;
            else if (addType.equals("A")) fujiAddress.DataCode = 0x04;
            else if (addType.equals("D")) fujiAddress.DataCode = 0x05;
            else if (addType.equals("S")) fujiAddress.DataCode = 0x08;
            else if (addType.startsWith("W")) {
                int add = Convert.ToInt32(addType.substring(1));
                if (add == 9) fujiAddress.DataCode = 0x09;
                else if (add >= 21 && add <= 26) fujiAddress.DataCode = (byte) add;
                else if (add >= 30 && add <= 109) fujiAddress.DataCode = (byte) add;
                else if (add >= 120 && add <= 123) fujiAddress.DataCode = (byte) add;
                else if (add == 125) fujiAddress.DataCode = (byte) add;
                else throw new Exception(StringResources.Language.NotSupportedDataType());
            } else
                throw new Exception(StringResources.Language.NotSupportedDataType());

            //switch (fujiAddress.DataCode)
            //{
            //	case 0x09:
            //	case 0x0A:
            //	case 0x0B:
            //	case 0x0C:
            //	case 0x0D:
            //	case 0x0E:
            //	case 0x19:
            //		{
            //			fujiAddress.Length = (ushort)(length > 1 ? length / 2 : length);
            //			break;
            //		}
            //	case 0x08:
            //		{
            //			fujiAddress.Length = (ushort)(length == 0 ? 1 : length * 2);
            //			break;
            //		}
            //}

            return OperateResultExOne.CreateSuccessResult(fujiAddress);
        } catch (Exception ex) {
            return new OperateResultExOne<FujiCommandSettingTypeAddress>(GetUnsupportedAddressInfo(address, ex));
        }
    }
}
