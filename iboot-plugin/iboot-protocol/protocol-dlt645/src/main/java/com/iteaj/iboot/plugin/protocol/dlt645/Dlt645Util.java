package com.iteaj.iboot.plugin.protocol.dlt645;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iot.utils.ByteUtil;

public class Dlt645Util {

    public static String validate(byte[] message, int prefix) {
        int respCode = (message[prefix + 8] & 0xFF);
        if(respCode >= 0xD0) {
            int errCode = message[prefix + 10] & 0xFF;
            Dlt645ErrCode code = Dlt645ErrCode.valueOf(errCode);
            return code.getDesc() + "[0x"+Integer.toHexString(errCode).toUpperCase()+"]";
        }

        int sum = 0;
        for (int i = prefix; i < message.length - 2; i++) {
            sum += message[i];
        }

        int validCode = sum % 256; // 校验码
        if(validCode == (message[message.length - 2] & 0xFF)) {
            return null;
        } else {
            return "报文校验码校验失败";
        }
    }

    public static Object getData(byte[] message, int prefix, Dlt645ProtocolType type) {
        byte[] bytes;
        if(type == Dlt645ProtocolType.readReq) {
            bytes = ByteUtil.subBytes(message, 14 + prefix, message.length - 2);
        } else {
            bytes = ByteUtil.subBytes(message, 10 + prefix, message.length - 2);
        }

        if(bytes.length == 0) {
            return null;
        }

        byte[] reverse = ArrayUtil.reverse(bytes);
        for (int i = 0; i < reverse.length; i++) {
            reverse[i] = (byte) (reverse[i] - 0x33);
        }

        String hexValue = ByteUtil.bytesToHex(reverse);
        if(type == Dlt645ProtocolType.readReq) {
            int item = message[prefix + 13] - 0x33;
            int childItem = message[prefix + 12] - 0x33;
            if(item == 0) {
                return Long.valueOf(hexValue) * 0.01;
            } else if(item == 2) {
                Dlt645DataItem dataItem = Dlt645DataItem.valueOf(childItem);
                if(dataItem != null) {
                    return Long.valueOf(hexValue) * dataItem.getResolution();
                }
            } else if(item == 5) {
                if(hexValue.length() % 8 == 0) {
                    StringBuilder sb = new StringBuilder();
                    int num = hexValue.length() / 8;
                    for (int i = 0; i < num; i++) {
                        String valueItem = hexValue.substring(i * 8, i * 8 + 8);
                        sb.append(Long.valueOf(valueItem) * 0.01).append(',');
                    }

                    return sb.deleteCharAt(sb.length() - 1).toString();
                }
            }
        }

        return hexValue;
    }

    public static String reverse(String data) {
        byte[] bytes = ByteUtil.hexToByte(data);
        byte[] reverse = ArrayUtil.reverse(bytes);
        return ByteUtil.bytesToHex(reverse);
    }

    public static String resolverDate(String date) {
        byte[] result = new byte[6];
        byte[] bytes = ByteUtil.hexToByte(date);
        result[0] = (byte) (bytes[5] + 0x33);
        result[1] = (byte) (bytes[4] + 0x33);
        result[2] = (byte) (bytes[3] + 0x33);
        result[3] = (byte) (bytes[2] + 0x33);
        result[4] = (byte) (bytes[1] + 0x33);
        result[5] = (byte) (bytes[0] + 0x33);
        return ByteUtil.bytesToHex(result);
    }

    public static String resolverData(String data) {
        if(StrUtil.isBlank(data)) {
            throw new NullPointerException("数据标识必填");
        }

        if(data.length() != 8) {
            throw new IllegalArgumentException("数据标识长度必须等于8");
        }

        byte[] result = new byte[4];
        byte[] bytes = ByteUtil.hexToByte(data);
        result[0] = (byte) (bytes[3] + 0x33);
        result[1] = (byte) (bytes[2] + 0x33);
        result[2] = (byte) (bytes[1] + 0x33);
        result[3] = (byte) (bytes[0] + 0x33);
        return ByteUtil.bytesToHex(result);
    }

    public static String resolverCommonData(String data) {
        byte[] bytes = ByteUtil.hexToByte(data);
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = (byte) (bytes[bytes.length - i - 1] + 0x33);
        }
        return ByteUtil.bytesToHex(result);
    }
}
