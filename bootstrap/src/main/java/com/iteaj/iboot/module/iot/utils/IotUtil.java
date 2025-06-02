package com.iteaj.iboot.module.iot.utils;

public class IotUtil {

    public static String getModelCodePrefix(String productCode) {
        if(productCode.length() <= 5) {
            return productCode + "_";
        }

        StringBuilder sb = new StringBuilder();
        final char[] chars = productCode.toCharArray();
        sb.append(chars[0]);
        int limit = productCode.length() <= 20 ? 4 : 5;
        for (int i = 0; i < chars.length; i++) {
            if(Character.isLetter(chars[i])) {
                if(i % limit == 0) {
                    sb.append(chars[i]);
                }
            }
        }

        return sb.append('_').toString();
    }
}
