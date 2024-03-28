package com.iteaj.framework.spi.iot;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SignalOrFieldValue {

    /**
     * 值
     */
    private Object value;

    /**
     * 点位地址
     */
    private String address;

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

    public static SignalOrFieldValue build(String address, String signalOrField, Object value) {
        return new SignalOrFieldValue(value, signalOrField).setAddress(address);
    }
}
