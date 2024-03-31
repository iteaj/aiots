package com.iteaj.framework.spi.iot;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口调用参数
 */
public class ProtocolModelApiInvokeParam {

    /**
     * 设备uid
     */
    private String uid;

    /**
     * 超时时间(毫秒)
     */
    private Long timeout;

    /**
     * 执行参数
     */
    private JSONObject param;

    /**
     * 执行配置
     */
    private Map config;

    public ProtocolModelApiInvokeParam() { }

    public ProtocolModelApiInvokeParam(String uid) {
        this(uid, new JSONObject());
    }

    public ProtocolModelApiInvokeParam(String uid, JSONObject param) {
        this.uid = uid;
        this.param = param;
    }

    public Boolean getBoolean(String key) {
        return param.getBoolean(key);
    }

    public Short getShort(String key) {
        return param.getShort(key);
    }

    public Integer getInteger(String key) {
        return param.getInteger(key);
    }

    public Float getFloat(String key) {
        return param.getFloat(key);
    }

    public Double getDouble(String key) {
        return param.getDouble(key);
    }

    public String getString(String key) {
        return param.getString(key);
    }

    public String getUid() {
        return uid;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getDeviceSn() {
        return this.getConfig(DeviceKey.DEVICE_SN_KEY);
    }

    public String getParentDeviceSn() {
        return this.getConfig(DeviceKey.PARENT_DEVICE_SN_KEY);
    }

    public DeviceKey buildKey() {
        return DeviceKey.build(getDeviceSn(), getParentDeviceSn());
    }

    public ProtocolModelApiInvokeParam setDeviceSn(String deviceSn) {
        return this.addConfig(DeviceKey.DEVICE_SN_KEY, deviceSn);
    }

    public ProtocolModelApiInvokeParam setParentDeviceSn(String parentDeviceSn) {
        return this.addConfig(DeviceKey.PARENT_DEVICE_SN_KEY, parentDeviceSn);
    }

    public JSONObject getParam() {
        return param;
    }

    public void setParam(JSONObject param) {
        this.param = param;
    }

    public ProtocolModelApiInvokeParam addParam(String key, Object value) {
        param.put(key, value); return this;
    }

    public Map getConfig() {
        return config;
    }

    public <T> T getConfig(String key) {
        return config != null ? (T) config.get(key) : null;
    }

    public ProtocolModelApiInvokeParam addConfig(String key, Object value) {
        if(this.config == null) {
            this.config = new HashMap();
        }

        this.config.put(key, value);
        return this;
    }

    public void setConfig(Map config) {
        this.config = config;
    }
}
