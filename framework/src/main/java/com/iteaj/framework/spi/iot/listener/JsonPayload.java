package com.iteaj.framework.spi.iot.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;

/**
 * json格式的负载
 */
public class JsonPayload implements IotEventPayload{

    private final JSONObject json;

    public JsonPayload() {
        this.json = new JSONObject();
    }

    public JsonPayload(Object value) {
        this.json = (JSONObject) JSON.toJSON(value);
    }

    public JSONObject getJson() {
        return json;
    }

    public <T> T get(String path) {
        JSONPath compile = JSONPath.compile(path);
        return (T) compile.eval(json);
    }

    public <T> T toBean(Class<T> clazz) {
        return this.json.toJavaObject(clazz);
    }
}
