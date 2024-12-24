package com.iteaj.plugin.protocol.plc.adapt;

import com.iteaj.iot.Protocol;
import com.iteaj.iot.plc.ReadAddress;

import java.util.List;

public interface PlcReadWriteProtocol extends Protocol {

    /**
     * 批量读取字节数组信息，需要指定地址和长度，返回原始的字节数组<br />
     * @param address 数据地址
     * @param length 数据长度
     */
    byte[] read(String address, short length);

    /**
     * 读取单个的bool数据信息<br />
     * @param address 数据地址
     */
    default Boolean readBool(String address) {
        boolean[] booleans = this.readBool(address, (short) 1);
        return booleans != null ? booleans[0] : null;
    }

    /**
     * 读取单个的bool数据信息<br />
     * @param address 数据地址
     * @param length 读取的数组长度
     */
    boolean[] readBool(String address, short length);

    /**
     * 读取16位的有符号的整型数据<br />
     * @param address 起始地址
     */
    default Short readInt16(String address) {
        short[] shorts = this.readInt16(address, (short) 1);
        return shorts != null ? shorts[0] : null;
    }

    /**
     * 读取16位的有符号整型数组<br />
     * @param address 起始地址
     * @param length 读取的数组长度
     */
    short[] readInt16(String address, short length);

    /**
     * 读取16位的无符号整型<br />
     * @param address 起始地址
     */
    default Integer readUInt16(String address) {
        int[] ints = this.readUInt16(address, (short) 1);
        return ints != null ? ints[0] : null;
    }

    /**
     * 读取16位的无符号整型数组<br />
     * @param address 起始地址
     * @param length 读取的数组长度
     */
    int[] readUInt16(String address, short length);

    /**
     * 读取32位的有符号整型<br />
     * @param address 起始地址
     */
    default Integer readInt32(String address) {
        int[] ints = this.readInt32(address, (short) 1);
        return ints != null ? ints[0] : null;
    }

    /**
     * 读取32位有符号整型数组<br />
     * @param address 起始地址
     * @param length 数组长度
     */
    int[] readInt32(String address, short length);

    /**
     * 读取32位的无符号整型<br />
     * @param address 起始地址
     */
    default Long readUInt32(String address) {
        long[] longs = this.readUInt32(address, (short) 1);
        return longs != null ? longs[0] : null;
    }

    /**
     * 读取32位的无符号整型数组<br />
     * @param address 起始地址
     * @param length 数组长度
     */
    long[] readUInt32(String address, short length);

    /**
     * 读取64位的有符号整型<br />
     * @param address 起始地址
     */
    default Long readInt64(String address) {
        long[] longs = this.readInt64(address, (short) 1);
        return longs != null ? longs[0] : null;
    }

    /**
     * 读取64位的有符号整型数组<br />
     * @param address 起始地址
     * @param length 数组长度
     */
    long[] readInt64(String address, short length);

    /**
     * 读取单浮点数据<br />
     * @param address 起始地址
     */
    default Float readFloat(String address) {
        float[] floats = this.readFloat(address, (short) 1);
        return floats != null ? floats[0] : null;
    }

    /**
     * 读取单浮点精度的数组<br />
     * @param address 起始地址
     * @param length 数组长度
     */
    float[] readFloat(String address, short length);

    /**
     * 读取双浮点的数据<br />
     * @param address 起始地址
     */
    default Double readDouble(String address) {
        double[] doubles = this.readDouble(address, (short) 1);
        return doubles != null ? doubles[0] : null;
    }

    /**
     * 读取双浮点数据的数组<br />
     * @param address 起始地址
     * @param length 数组长度
     */
    double[] readDouble(String address, short length);

    /**
     * 读取字符串数据，默认为最常见的ASCII编码<br />
     * @param address 起始地址
     * @param length 数据长度
     */
    default String readString(String address, short length) {
        return this.readString(address, length, "UTF-8");
    }

    /**
     * 使用指定的编码，读取字符串数据<br />
     * @param address 起始地址
     * @param length 数据长度
     * @param encoding 指定的自定义的编码
     */
    String readString(String address, short length, String encoding);

    /**
     * 写入原始的byte数组数据到指定的地址，返回是否写入成功<br />
     * @param address 起始地址
     * @param value 写入值
     */
    void write(String address, byte[] value);

    /**
     * 批量写入 {@link Boolean} 数组数据，返回是否成功<br />
     * @param address 起始地址
     * @param value 要写入的数据 长度为8的倍数
     */
    void write(String address, boolean[] value);

    /**
     * 写入单个的 {@link Boolean} 数据，返回是否成功<br />
     * @param address 起始地址
     * @param value 写入值
     */
    default void write(String address, boolean value) {
        this.write(address, new boolean[]{value});
    }

    /**
     * 写入short数据，返回是否成功<br />
     * @param address 起始地址
     * @param value 写入值
     */
    void write(String address, short value);

    /**
     * 写入short数组，返回是否成功<br />
     * @param address 起始地址
     * @param values 写入值
     */
    void write(String address, short[] values);

    /**
     * 写入int数据，返回是否成功<br />
     * @param address 起始地址
     * @param value 写入值
     */
    void write(String address, int value);

    /**
     * 写入int[]数组，返回是否成功<br />
     * @param address 起始地址
     * @param values 写入值
     */
    void write(String address, int[] values);

    /**
     * 写入long数据，返回是否成功<br />
     * @param address 起始地址
     * @param value 写入值
     */
    void write(String address, long value);

    /**
     * 写入long数组，返回是否成功<br />
     * @param address 起始地址
     * @param values 写入值
     */
    void write(String address, long[] values);

    /**
     * 写入float数据，返回是否成功<br />
     * @param address 起始地址
     * @param value 写入值
     */
    void write(String address, float value);

    /**
     * 写入float数组，返回是否成功<br />
     * @param address 起始地址
     * @param values 写入值
     */
    void write(String address, float[] values);

    /**
     * 写入double数据，返回是否成功<br />
     * @param address 起始地址
     * @param value 写入值
     */
    void write(String address, double value);

    /**
     * 写入double数组，返回是否成功<br />
     * @param address 起始地址
     * @param values 写入值
     */
    void write(String address, double[] values);

    /**
     * 写入字符串信息，编码为ASCII<br />
     * @param address 起始地址
     * @param value 写入值
     */
    void write(String address, String value);

    /**
     * 写入字符串信息，需要指定的编码信息<br />
     * @param address 起始地址
     * @param value 写入值
     * @param encoding 指定的编码信息
     */
    void write(String address, String value, String encoding);
}
