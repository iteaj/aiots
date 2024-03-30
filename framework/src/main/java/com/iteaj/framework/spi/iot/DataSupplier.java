package com.iteaj.framework.spi.iot;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DataSupplier {

    /**
     * 设备key信息
     */
    private DeviceKey key;

    /**
     * 协议码
     */
    private String protocolCode;

    /**
     * 采集时间
     */
    private Date collectTime;

    /**
     * 点位或者字段列表值
     */
    private List<SignalOrFieldValue> values;

    public DataSupplier(String protocolCode) {
        this.protocolCode = protocolCode;
    }

    public DataSupplier addValue(SignalOrFieldValue value) {
        if(values == null) {
            values = new ArrayList<>();
        }

        values.add(value); return this;
    }

    public DataSupplier addValue(String signalOrField, Object value) {
        return this.addValue(SignalOrFieldValue.build(signalOrField, value));
    }

    public DataSupplier addValue(String address, String signalOrField, Object value) {
        return this.addValue(SignalOrFieldValue.build(address, signalOrField, value));
    }

    public DataSupplier build(Date collectTime) {
        this.collectTime = collectTime;
        if(CollectionUtil.isNotEmpty(this.getValues())) {
            this.getValues().forEach(item -> item.setCollectTime(collectTime));
        }
        return this;
    }
}
