package com.iteaj.framework.spi.iot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchOfPoint {

    /**
     * 指令
     */
    private String direct;

    /**
     * 地址
     */
    private String address;

    /**
     * 上行模型属性
     */
    private UpModelAttr modelAttr;

    /**
     * 位模型属性配置
     * @see UpModelAttr#isBit()
     */
    private List<UpModelAttr> bitAttrs;

    /**
     * 下行配置
     */
    private Map<String, String> downConfig = new HashMap<>();

    public BatchOfPoint() { }

    public BatchOfPoint(String direct, String address) {
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
        return this.modelAttr.getId();
    }

    public String getAttrField() {
        return this.modelAttr.getField();
    }

    public String getAttrName() {
        return this.modelAttr.getName();
    }

    public String getDownConfig(String key) {
        return downConfig.get(key);
    }

    public BatchOfPoint addDownConfig(String key, String value) {
        downConfig.put(key, value);
        return this;
    }

    public UpModelAttr getModelAttr() {
        return modelAttr;
    }

    public void setModelAttr(UpModelAttr modelAttr) {
        this.modelAttr = modelAttr;
    }

    public List<UpModelAttr> getBitAttrs() {
        return bitAttrs;
    }

    public BatchOfPoint setBitAttrs(List<UpModelAttr> bitAttrs) {
        this.bitAttrs = bitAttrs; return this;
    }
}
