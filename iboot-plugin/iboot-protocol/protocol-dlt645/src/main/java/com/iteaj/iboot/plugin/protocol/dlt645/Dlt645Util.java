package com.iteaj.iboot.plugin.protocol.dlt645;

import cn.hutool.core.util.ArrayUtil;
import com.iteaj.iot.utils.ByteUtil;

public class Dlt645Util {

    public static boolean validate(byte[] message, int prefix) {
        int sum = 0;
        for (int i = prefix; i < message.length - 2; i++) {
            sum += message[i];
        }

        int validCode = sum % 256; // 校验码
        if(validCode == (message[message.length - 2] & 0xFF)) {
            return true;
        } else {
            return false;
        }
    }

    public static byte[] getData(byte[] message, int prefix) {
        byte[] bytes = ByteUtil.subBytes(message, 14 + prefix, message.length - 2);

        byte[] reverse = ArrayUtil.reverse(bytes);
        for (int i = 0; i < reverse.length; i++) {
            reverse[i] = (byte) (reverse[i] - 0x33);
        }

        return reverse;
    }
}
