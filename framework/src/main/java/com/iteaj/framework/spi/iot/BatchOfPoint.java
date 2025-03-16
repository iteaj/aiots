package com.iteaj.framework.spi.iot;

import java.util.HashMap;
import java.util.Map;

public class BatchOfPoint {

    /**
     * 属性id
     */
    private Long attrId;

    /**
     * 属性字段
     */
    private String attrField;

    /**
     * 指令
     */
    private String direct;

    /**
     * 地址
     */
    private String address;

    /**
     * 下行配置
     */
    private Map<String, String> downConfig = new HashMap<>();

    public BatchOfPoint() { }

    public BatchOfPoint(Long attrId, String attrField, String direct, String address) {
        this.attrId = attrId;
        this.attrField = attrField;
        this.direct = direct;
        this.address = address;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public String getAttrField() {
        return attrField;
    }

    public void setAttrField(String attrField) {
        this.attrField = attrField;
    }

    public String getDownConfig(String key) {
        return downConfig.get(key);
    }

    public BatchOfPoint addDownConfig(String key, String value) {
        downConfig.put(key, value);
        return this;
    }
}
