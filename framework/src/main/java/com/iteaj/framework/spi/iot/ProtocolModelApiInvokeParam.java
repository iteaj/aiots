package com.iteaj.framework.spi.iot;

import com.alibaba.fastjson.JSONObject;

public class ProtocolModelApiInvokeParam {

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 参数
     */
    private JSONObject param;

    public ProtocolModelApiInvokeParam(String deviceSn) {
        this(deviceSn, new JSONObject());
    }

    public ProtocolModelApiInvokeParam(String deviceSn, JSONObject param) {
        this.param = param;
        this.deviceSn = deviceSn;
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

}
