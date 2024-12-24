package com.iteaj.plugin.protocol.plc.core.types;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;

public class Extension {
    /**
     * 获取Byte数组的第 bytIndex 个位置的，boolIndex偏移的bool值<br />
     * Get the bool value of the bytIndex position of the Byte array and the boolIndex offset
     * @param bytes 字节数组信息
     * @param bytIndex 字节的偏移位置
     * @param boolIndex 指定字节的位偏移
     * @return bool值
     */
    public static boolean GetBoolValue( byte[] bytes, int bytIndex, int boolIndex ) {
        return SoftBasic.BoolOnByteIndex(bytes[bytIndex], boolIndex);
    }

    /**
     * 获取Byte数组的第 boolIndex 偏移的bool值，这个偏移值可以为 10，就是第 1 个字节的 第3位 <br />
     * Get the bool value of the boolIndex offset of the Byte array. The offset value can be 10, which is the third bit of the first byte
     * @param bytes 字节数组信息
     * @param boolIndex 指定字节的位偏移
     * @return bool值
     */
    public static boolean GetBoolByIndex( byte[] bytes, int boolIndex ) {
        return SoftBasic.BoolOnByteIndex(bytes[boolIndex / 8], boolIndex % 8);
    }

    /**
     * 获取Byte的第 boolIndex 偏移的bool值，比如3，就是第4位 <br />
     * Get the bool value of Byte's boolIndex offset, such as 3, which is the 4th bit
     * @param byt 字节信息
     * @param boolIndex 指定字节的位偏移
     * @return bool值
     */
    public static boolean GetBoolByIndex( byte byt, int boolIndex ) {
        return SoftBasic.BoolOnByteIndex(byt, boolIndex % 8);
    }

    /**
     * 获取short类型数据的第 boolIndex (从0起始)偏移的bool值，比如3，就是第4位 <br />
     * Get the bool value of the boolIndex (starting from 0) offset of the short type data, such as 3, which is the 4th bit
     * @param value short数据值
     * @param boolIndex 位偏移索引，从0开始
     * @return bool值
     */
    public static boolean GetBoolByIndex( short value, int boolIndex ) {
        return GetBoolByIndex(BitConverter.GetBytes(value), boolIndex);
    }

    /**
     * 获取int类型数据的第 boolIndex (从0起始)偏移的bool值，比如3，就是第4位 <br />
     * Get the bool value of the boolIndex (starting from 0) offset of the int type data, such as 3, which is the 4th bit
     * @param value int数据值
     * @param boolIndex 位偏移索引，从0开始
     * @return bool值
     */
    public static boolean GetBoolByIndex( int value, int boolIndex ) {
        return GetBoolByIndex(BitConverter.GetBytes(value), boolIndex);
    }

    /**
     * 获取long类型数据的第 boolIndex (从0起始)偏移的bool值，比如3，就是第4位 <br />
     * Get the bool value of the boolIndex (starting from 0) offset of the long type data, such as 3, which is the 4th bit
     * @param value long数据值
     * @param boolIndex 位偏移索引，从0开始
     * @return bool值
     */
    public static boolean GetBoolByIndex( long value, int boolIndex ) {
        return GetBoolByIndex(BitConverter.GetBytes(value), boolIndex);
    }

    private static String[] CreateSplits( String value ){
        if (value.startsWith("[")) value = value.substring(1);
        if (value.endsWith("]")) value = value.substring(0, value.length() - 1);
        return value.split("[,;]");
    }

    /**
     * 字符串转bool数组信息
     * @param value 字符串信息
     * @return bool数组
     */
    public static boolean[] StringToBoolArray( String value ) {
        String[] splits = CreateSplits(value);
        boolean[] array = new boolean[splits.length];
        for (int i = 0; i < array.length; i++) {
            if (splits[i].equals("1")) array[i] = true;
            else if (splits[i].equals("0")) array[i] = false;
            else array[i] = Boolean.parseBoolean(splits[i]);
        }
        return array;
    }

    public static byte[] StringToByteArray( String value ) throws Exception {
        String[] splits = CreateSplits(value);
        byte[] array = new byte[splits.length];
        for (int i = 0; i < array.length; i++) {
            short tmp = Short.parseShort(splits[i]);
            if(tmp < 0 || tmp > 255){
                throw new Exception("Value must be between 0 - 255");
            }
            array[i] = (byte) tmp;
        }
        return array;
    }

    /**
     * 字符串转short数组信息
     * @param value 字符串信息
     * @return short数组
     */
    public static short[] StringToShortArray( String value ) {
        String[] splits = CreateSplits(value);
        short[] array = new short[splits.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Short.parseShort(splits[i]);
        }
        return array;
    }
    /**
     * 字符串转ushort数组信息
     * @param value 字符串信息
     * @return short数组
     */
    public static short[] StringToUShortArray( String value ) throws Exception {
        String[] splits = CreateSplits(value);
        short[] array = new short[splits.length];
        for (int i = 0; i < array.length; i++) {
            int tmp = Integer.parseInt(splits[i]);
            if(tmp < 0 || tmp > 65535){
                throw new Exception("Value must be between 0 - 65535");
            }
            array[i] = (short)tmp;
        }
        return array;
    }

    /**
     * 字符串转int数组信息
     * @param value 字符串信息
     * @return int数组
     */
    public static int[] StringToIntArray( String value ) {
        String[] splits = CreateSplits(value);
        int[] array = new int[splits.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(splits[i]);
        }
        return array;
    }
    /**
     * 字符串转uint数组信息
     * @param value 字符串信息
     * @return int数组
     */
    public static int[] StringToUIntArray( String value ) throws Exception {
        String[] splits = CreateSplits(value);
        int[] array = new int[splits.length];
        for (int i = 0; i < array.length; i++) {
            long tmp = Long.parseLong(splits[i]);
            if(tmp < 0 || tmp > 4294967295L){
                throw new Exception("Value must be between 0 - 4294967295L");
            }
            array[i] = (int)tmp;
        }
        return array;
    }

    /**
     * 字符串转long数组信息
     * @param value 字符串信息
     * @return long数组
     */
    public static long[] StringToLongArray( String value ) {
        String[] splits = CreateSplits(value);
        long[] array = new long[splits.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Long.parseLong(splits[i]);
        }
        return array;
    }

    /**
     * 字符串转float数组信息
     * @param value 字符串信息
     * @return float数组
     */
    public static float[] StringToFloatArray( String value ) {
        String[] splits = CreateSplits(value);
        float[] array = new float[splits.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Float.parseFloat(splits[i]);
        }
        return array;
    }

    /**
     * 字符串转double数组信息
     * @param value 字符串信息
     * @return double数组
     */
    public static double[] StringToDoubleArray( String value ) {
        String[] splits = CreateSplits(value);
        double[] array = new double[splits.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Double.parseDouble(splits[i]);
        }
        return array;
    }

    /**
     * 判断地址是否以指定的字符串加数字起始的情况
     * @param value 等待校验的地址
     * @param heads 校验的起始地址信息
     * @return 是否指定地址开始
     */
    public static boolean StartsWithAndNumber( String value, String[] heads ) {
        if (heads == null) return false;
        for (int i = 0; i < heads.length; i++) {
            if (value.matches("^" + heads[i] + "[0-9]+[\\S\\s]+")) return true;
        }
        return false;
    }
}
