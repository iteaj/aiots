package com.iteaj.framework.spi.iot;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iot.utils.UniqueIdGen;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class DataSupplier {

    /**
     * 每次采集唯一标识
     */
    private String cid;

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
    private Map<String, SignalOrFieldValue> values;

    public DataSupplier(String protocolCode, DeviceKey key) {
        this(protocolCode, key, String.valueOf(UniqueIdGen.nextLong()));
    }

    public DataSupplier(String protocolCode, DeviceKey key, String cid) {
        this.cid = cid;
        this.key = key;
        this.protocolCode = protocolCode;
        this.values = new LinkedHashMap<>();
    }

    public SignalOrFieldValue getFieldValue(String field) {
        return values.get(field);
    }

    public Collection<SignalOrFieldValue> getValues() {
        return values.values();
    }

    public DataSupplier setValues(Collection<SignalOrFieldValue> values) {
        if(CollectionUtil.isNotEmpty(values)) {
            values.forEach(this::addValue);
        }

        return this;
    }

    public DataSupplier addValue(SignalOrFieldValue value) {
        values.put(value.getSignalOrField(), value); return this;
    }

    public DataSupplier addValue(Long id, String signalOrField, Object value) {
        return this.addValue(SignalOrFieldValue.build(id, signalOrField, this.collectTime, value));
    }

    public DataSupplier addValue(Long id, String address, String signalOrField, Object value) {
        return this.addValue(SignalOrFieldValue.build(id, signalOrField, this.collectTime, value, address));
    }

    public DataSupplier addValue(Long id, String address, String signalOrField, CollectStatus status) {
        return this.addValue(SignalOrFieldValue.build(id, signalOrField, this.collectTime, status, address));
    }

    public DataSupplier addValue(Long id, String address, String signalOrField, Object value, Date collectTime) {
        return this.addValue(SignalOrFieldValue.build(id, signalOrField, collectTime, value, address));
    }

    public DataSupplier build(Date collectTime) {
        this.collectTime = collectTime;
        if(CollectionUtil.isNotEmpty(this.getValues())) {
            this.getValues().forEach(item -> {
                if(item.getCollectTime() == null) {
                    item.setCollectTime(collectTime);
                }
            });
        }

        return this;
    }
}
