package com.iteaj.framework.script;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public interface JavaScriptValueProvide {

    default boolean isJSValue(Object value) {
        return value instanceof ScriptObjectMirror;
    }

    default boolean isJSArray(ScriptObjectMirror value) {
        return value.isArray();
    }

    default String jsArrayToString(ScriptObjectMirror value) {
        return ArrayUtil.toString((value).values());
    }

    default String jsObjectToString(ScriptObjectMirror value) {
        final JSONObject object = new JSONObject();
        (value).entrySet().forEach(item -> object.put(item.getKey(), item.getValue()));
        return object.toJSONString();
    }

    default String jsMirrorToString(ScriptObjectMirror value) {
        if(isJSArray(value)) {
            return jsArrayToString(value);
        } else {
            return jsObjectToString(value);
        }
    }
}
