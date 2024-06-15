package com.iteaj.framework.spi.iot.listener;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * json格式的负载
 */
public class JsonPayload implements IotEventPayload{

    private JSONObject json;

    public JsonPayload() {
        this.json = JSONUtil.createObj();
    }

    public JsonPayload(Object value) {
        if(value instanceof JSONObject) {
            json = (JSONObject) value;
        } else {
            this.json = JSONUtil.parseObj(value);
        }
    }

    public JSONObject getJson() {
        return json;
    }

    public <T> T get(String path) {
        return (T) this.json.getByPath(path);
    }

    public <T> T toBean(Class<T> clazz) {
        return this.json.toBean(clazz);
    }
}
