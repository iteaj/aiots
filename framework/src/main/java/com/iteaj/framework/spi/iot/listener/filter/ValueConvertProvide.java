package com.iteaj.framework.spi.iot.listener.filter;

import com.iteaj.iot.utils.ByteUtil;

public class ValueConvertProvide {

    public byte[] toBytes(Object value) {
        if(value instanceof Number) {
            if(value instanceof Short) {
                return ByteUtil.getBytes((Short) value);
            } else if(value instanceof Integer) {
                return ByteUtil.getBytes((Integer) value);
            } else if(value instanceof Long) {
                return ByteUtil.getBytes((Long) value);
            } else if(value instanceof Float) {
                return ByteUtil.getBytes((Float) value);
            } else if(value instanceof Double) {
                return ByteUtil.getBytes((Double) value);
            } else if(value instanceof Byte) {
                return ByteUtil.getBytes((Byte) value);
            }
        } else if(value instanceof String) {
            return ByteUtil.getBytes((String) value);
        }

        return null;
    }

    /**
     * 值转换成16进制
     * @param value
     * @return
     */
    public String toHex(Object value) {
        return ByteUtil.bytesToHex(toBytes(value));
    }
}
