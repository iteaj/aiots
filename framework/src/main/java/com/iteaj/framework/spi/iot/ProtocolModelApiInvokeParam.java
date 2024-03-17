package com.iteaj.framework.spi.iot;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口调用参数
 */
public class ProtocolModelApiInvokeParam {

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 超时时间
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

    public ProtocolModelApiInvokeParam(String deviceSn) {
        this(deviceSn, new JSONObject());
    }

    public ProtocolModelApiInvokeParam(String deviceSn, JSONObject param) {
        this.param = param;
        this.deviceSn = deviceSn;
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

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
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
