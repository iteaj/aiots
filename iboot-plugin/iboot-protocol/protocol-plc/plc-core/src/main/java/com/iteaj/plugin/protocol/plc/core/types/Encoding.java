package com.iteaj.plugin.protocol.plc.core.types;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 系统的编码资源信息
 */
public class Encoding {
    /**
     * 从缓存里，获取相关的字符串信息
     * @param buffer 缓存信息
     * @param index 起始索引
     * @param length 长度信息
     * @return 最终的字符串
     */
    public String GetString( byte[] buffer, int index, int length ){
        return new String(buffer, index, length, getCharSet());
    }

    /**
     * 从缓存里，获取相关的字符串信息
     * @param buffer 缓解信息
     * @return 最终的字符串
     */
    public String GetString( byte[] buffer ){
        return new String(buffer, getCharSet());
    }

    /**
     * 从字符串里获取到原始字节数据
     * @param value 字符串信息
     * @return 字节信息
     */
    public byte[] GetBytes( String value ){
        if (value == null) return  null;
        return value.getBytes(getCharSet());
    }

    /**
     * 从字符串里获取到原始字节数据
     * @param value 字符串信息
     * @return 字节信息
     */
    public List<Byte> GetBytesList( String value ) {
        if (value == null) return null;
        byte[] buffer = value.getBytes(getCharSet());
        List<Byte> list = new List<>();
        for (int i = 0; i < buffer.length; i++) {
            list.Add(buffer[i]);
        }
        return list;
    }

    /**
     * 获取编码信息
     * @return 编码
     */
    public Charset getCharSet(){
        return StandardCharsets.US_ASCII;
    }

    /**
     * ASCII编码信息
     */
    public static Encoding ASCII = new Encoding();

    /**
     * UTF8编码信息
     */
    public static Encoding UTF8 = new Encoding(){
        @Override
        public Charset getCharSet() {
            return StandardCharsets.UTF_8;
        }
    };
}
