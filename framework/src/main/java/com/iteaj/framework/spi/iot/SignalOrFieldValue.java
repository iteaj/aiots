package com.iteaj.framework.spi.iot;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

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
     * 采集时间
     */
    private Date collectTime;

    /**
     * 点位或者字段
     * 值必须在同一个{@link DeviceKey}下唯一
     */
    private String signalOrField;

    public SignalOrFieldValue(Object value, String signalOrField, Date collectTime) {
        this.value = value;
        this.collectTime = collectTime;
        this.signalOrField = signalOrField;
    }

    public static SignalOrFieldValue build(String signalOrField, Object value) {
        return build(signalOrField, (Date) null, value);
    }

    public static SignalOrFieldValue build(String signalOrField, Date collectTime, Object value) {
        return new SignalOrFieldValue(value, signalOrField, collectTime);
    }

    public static SignalOrFieldValue build(String address, String signalOrField, Object value) {
        return build(address, signalOrField, null, value);
    }

    public static SignalOrFieldValue build(String address, String signalOrField, Date collectTime, Object value) {
        return new SignalOrFieldValue(value, signalOrField, collectTime).setAddress(address);
    }
}
