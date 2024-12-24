package com.iteaj.plugin.protocol.plc.core.types;

public class Array {
    public static void Copy( byte[] source, int sourceIndex, byte[] target, int targetIndex, int length ){
        System.arraycopy(source, sourceIndex, target, targetIndex, length);
    }


    /**
     * 从bool数组中获取等效长度的byte数组，并指定true，false不同状态下的转换值信息
     * @param values bool数组信息
     * @param trueValue 为true时的转换值
     * @param falseValue 为false时的转换值
     * @return 结果消息
     */
    public static byte[] GetByteFromBoolArray( boolean[] values, int trueValue, int falseValue ) {
        if (values == null) return null;
        if (values.length == 0) return new byte[0];
        byte[] buffer = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i])
                buffer[i] = (byte) trueValue;
            else
                buffer[i] = (byte) falseValue;
        }
        return buffer;
    }

    /**
     * 从bool数组中获取等效长度的byte数组，并指定true，false 转换后分别为 1, 0
     * @param values bool数组信息
     * @return 结果消息
     */
    public static byte[] GetByteFromBoolArray( boolean[] values ){
        return GetByteFromBoolArray(values, (byte) 0x01, (byte)0x00);
    }

    /**
     * 从byte数组里，获取等效长度的bool数组数据，并指定什么情况为true
     * @param values byte数组信息
     * @param trueValue 为true的情况
     * @return bool数组结果
     */
    public static boolean[] GetBoolArrayFromBytes( byte[] values, int trueValue ) {
        if (values == null) return null;
        if (values.length == 0) return new boolean[0];
        boolean[] buffer = new boolean[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == trueValue)
                buffer[i] = true;
            else
                buffer[i] = false;
        }
        return buffer;
    }

}
