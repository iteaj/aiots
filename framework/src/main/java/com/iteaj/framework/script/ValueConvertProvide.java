package com.iteaj.framework.script;

import com.iteaj.iot.utils.ByteUtil;

import java.util.Arrays;
import java.util.List;

public class ValueConvertProvide implements JavaScriptValueProvide, LoggerMethodProvide{

    private final static String NAME = "属性解析器";
    public static ValueConvertProvide INSTANCE = new ValueConvertProvide();
    protected ValueConvertProvide() {}
    public static List<ScriptComplete> VALUE_CONVERT_COMPLETES = Arrays.asList(
            new ScriptComplete("methods.toHex(value)", "将基础类型值转成16进制",2),
            new ScriptComplete("methods.toBytes(value)", "将基础类型值转成字节数组",2)
    );

    public byte[] toBytes(Object value) {
        if(value == null) return null;
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

    @Override
    public String getSubType() {
        return NAME;
    }
}
