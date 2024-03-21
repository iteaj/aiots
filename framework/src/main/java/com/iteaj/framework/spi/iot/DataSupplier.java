package com.iteaj.framework.spi.iot;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DataSupplier {

    /**
     * 设备key信息
     */
    private DeviceKey key;

    /**
     * 点位或者字段列表值
     */
    private List<SignalOrFieldValue> values;

    public DataSupplier addValue(SignalOrFieldValue value) {
        if(values == null) {
            values = new ArrayList<>();
        }

        values.add(value); return this;
    }

    public DataSupplier addValue(String signalOrField, Object value) {
        return this.addValue(SignalOrFieldValue.build(signalOrField, value));
    }
}
