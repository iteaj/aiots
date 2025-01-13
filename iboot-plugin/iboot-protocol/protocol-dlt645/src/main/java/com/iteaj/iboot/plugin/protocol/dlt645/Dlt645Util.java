package com.iteaj.iboot.plugin.protocol.dlt645;

import cn.hutool.core.util.ArrayUtil;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iot.utils.ByteUtil;

public class Dlt645Util {

    public static String validate(byte[] message, int prefix) {
        int respCode = (message[prefix + 8] & 0xFF);
        if(respCode >= 0xD0) {
            return "从站异常应答";
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

    public static byte[] getData(byte[] message, int prefix, Dlt645ProtocolType type) {
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

        return reverse;
    }
}
