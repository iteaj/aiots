package com.iteaj.framework.spi.iot;

import com.alibaba.fastjson.JSONObject;

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
}
