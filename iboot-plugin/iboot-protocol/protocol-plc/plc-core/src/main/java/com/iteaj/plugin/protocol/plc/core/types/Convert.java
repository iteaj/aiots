package com.iteaj.plugin.protocol.plc.core.types;

public class Convert {

    /**
     * 将原始字节数据转为base64编码的字符串
     * @param value 原始字节
     * @return base64编码的字符串
     */
    public static String ToBase64String(byte[] value) {
        return java.util.Base64.getEncoder().encodeToString(value);
    }

    /**
     * 将base64编码的字符串转为原始字节数据
     * @param value base64编码的字符串
     * @return 原始字节数据
     */
    public static byte[] FromBase64String(String value) {
        return java.util.Base64.getDecoder().decode(value);
    }

    public static int ToInt32( String value ){
        return Integer.parseInt(value);
    }

    public static int ToInt32( String value, int fromBase ){
        return Integer.parseInt(value, fromBase);
    }

    public static String ToString( int value, int toBase ) {
        if (toBase == 2) return Integer.toBinaryString(value);
        if (toBase == 8) return Integer.toOctalString(value);
        if (toBase == 10) return String.valueOf(value);
        if (toBase == 16) return Integer.toHexString(value);
        return String.valueOf(value);
    }
}
