package com.iteaj.framework.spi.iot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignalOrFieldValue {

    /**
     * 值
     */
    private Object value;

    /**
     * 点位或者字段
     * 值必须在同一个{@link DeviceKey}下唯一
     */
    private String signalOrField;

    public SignalOrFieldValue(Object value, String signalOrField) {
        this.value = value;
        this.signalOrField = signalOrField;
    }

    public static SignalOrFieldValue build(String signalOrField, Object value) {
        return new SignalOrFieldValue(value, signalOrField);
    }
}
