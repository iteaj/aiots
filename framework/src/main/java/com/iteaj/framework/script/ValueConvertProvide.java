package com.iteaj.framework.script;

import com.iteaj.iot.utils.ByteUtil;

import java.util.Arrays;
import java.util.List;

public class ValueConvertProvide implements JavaScriptValueProvide, LoggerMethodProvide{

    public final static String NAME = "属性解析器";
    public static ValueConvertProvide INSTANCE = new ValueConvertProvide();
    protected ValueConvertProvide() {}
    public static List<ScriptComplete> VALUE_CONVERT_COMPLETES = Arrays.asList(
            new ScriptComplete("methods.toHex(value)", "将基础类型值转成16进制",2),
            new ScriptComplete("methods.toBytes(value)", "将基础类型值转成字节数组",2),
            new ScriptComplete("methods.toString(value)", "值转成字符串",2),
            new ScriptComplete("methods.isBytes(value)", "是否为字节数组类型",2),
            new ScriptComplete("methods.isNull(value)", "是否是空值",2),
            new ScriptComplete("methods.isNumber(value)", "是否是数值类型",2),
            new ScriptComplete("methods.getLength(value)", "获取字节数组长度值",2),
            new ScriptComplete("methods.getInt(value, offset)", "从字节数组的offset位置开始读取一个int值",2),
            new ScriptComplete("methods.getByte(value, offset)", "从字节数组的offset位置开始读取一个byte值",2),
            new ScriptComplete("methods.getShort(value, offset)", "从字节数组的offset位置开始读取一个Short值",2),
            new ScriptComplete("methods.getLong(value, offset)", "从字节数组的offset位置开始读取一个Long值",2),
            new ScriptComplete("methods.getFloat(value, offset)", "从字节数组的offset位置开始读取一个Float值",2),
            new ScriptComplete("methods.getDouble(value, offset)", "从字节数组的offset位置开始读取一个Double值",2),
            new ScriptComplete("methods.getString(value, offset, length)", "从字节数组的offset位置开始读取length长的字符串值",2)
    );

    public boolean isBytes(Object value) {
        return value instanceof byte[];
    }

    public boolean isNull(Object value) {
        return value == null;
    }

    public boolean isNumber(Object value) {
        return value instanceof Number;
    }

    public int getLength(Object value) {
        byte[] bytes = toBytes(value);
        return bytes != null ? bytes.length : 0;
    }

    public int getInt(Object value, int offset) {
        byte[] bytes = toBytes(value);
        if(!isNull(bytes)) {
            return ByteUtil.bytesToInt(bytes, offset);
        } else {
            throw new NullPointerException("要解析的字节数组为null");
        }
    }

    public short getShort(Object value, int offset) {
        byte[] bytes = toBytes(value);
        if(!isNull(bytes)) {
            return ByteUtil.bytesToShort(bytes, offset);
        } else {
            throw new NullPointerException("要解析的字节数组为null");
        }
    }

    public long getLong(Object value, int offset) {
        byte[] bytes = toBytes(value);
        if(!isNull(bytes)) {
            return ByteUtil.bytesToLong(bytes, offset);
        } else {
            throw new NullPointerException("要解析的字节数组为null");
        }
    }

    public float getFloat(Object value, int offset) {
        byte[] bytes = toBytes(value);
        if(!isNull(bytes)) {
            return ByteUtil.bytesToFloat(bytes, offset);
        } else {
            throw new NullPointerException("要解析的字节数组为null");
        }
    }

    public double getDouble(Object value, int offset) {
        byte[] bytes = toBytes(value);
        if(!isNull(bytes)) {
            return ByteUtil.bytesToDouble(bytes, offset);
        } else {
            throw new NullPointerException("要解析的字节数组为null");
        }
    }

    public String getString(Object value, int offset, int length) {
        byte[] bytes = toBytes(value);
        if(!isNull(bytes)) {
            return ByteUtil.bytesToString(bytes, offset, length);
        } else {
            throw new NullPointerException("要解析的字节数组为null");
        }
    }

    public byte[] toBytes(Object value) {
        if(value == null) return null;
        if(value instanceof byte[]) {
            return (byte[]) value;
        } else if(value instanceof Number) {
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
        } else if(value instanceof Boolean) {
            return new byte[] { (byte) ((Boolean) value ? 1 : 0) };
        }

        warn("-", "属性解析器只支持基本数据类型", value.getClass().getSimpleName());
        return null;
    }

    /**
     * 值转换成16进制
     * @param value
     * @return
     */
    public String toHex(Object value) {
        byte[] bytes = toBytes(value);
        return bytes != null ? ByteUtil.bytesToHex(bytes) : "";
    }

    public String toString(Object value) {
        if(isBytes(value)) {
            return ByteUtil.bytesToString((byte[]) value);
        } else {
            return value != null ? value.toString() : "";
        }
    }

    @Override
    public String getSubType() {
        return NAME;
    }
}
