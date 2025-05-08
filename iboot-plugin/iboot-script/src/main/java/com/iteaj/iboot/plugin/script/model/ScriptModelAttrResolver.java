package com.iteaj.iboot.plugin.script.model;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.script.*;
import com.iteaj.framework.spi.iot.DataValueResolver;
import com.iteaj.framework.spi.iot.DataValueResolverFactory;
import com.iteaj.framework.spi.iot.UpModelAttr;
import com.iteaj.framework.spi.iot.listener.filter.RealtimeListenerFilter;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * 模型属性的脚本解析
 */
public class ScriptModelAttrResolver implements DataValueResolver {

    private final String key;
    private final String name;
    private final ScriptConfig script;
    private final ScriptInterface scriptInterface;

    public ScriptModelAttrResolver(String key, String name, ScriptConfig script) {
        this.key = key;
        this.name = name;
        this.script = script;
        this.scriptInterface = ScriptManager.get(script.getLanguage());
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Object resolver(UpModelAttr attr, Object value) {
        if(this.scriptInterface != null) {
            final Object exec;
            try {
                exec = scriptInterface.exec(ScriptContext.build(this.getKey(), script.getScript(), script.getInvoke())
                        .addParam("value", value).addParam("model", attr)
                        .addParam("methods", RealtimeListenerFilter.convertProvide));
            } catch (ScriptExecException e) {
                RealtimeListenerFilter.convertProvide.error(attr.getField(), "属性解析脚本执行异常 脚本: {}/{} - 解析值: {}", name, script.getLanguage(), value, e);
                throw e;
            }

            Object resolver = doResolver(exec);
            RealtimeListenerFilter.convertProvide.debug(attr.getField(), "执行属性解析脚本 脚本: {}/{} - 解析前/后值: {}/{}", name, script.getLanguage(), value, resolver);
            return resolver;
        } else {
            RealtimeListenerFilter.convertProvide.warn(attr.getField(), "属性解析脚本引擎不存在 脚本: {}/{} - 解析值: {}", name, script.getLanguage(), value);
        }

        // 使用默认解析器解析
        return DataValueResolverFactory.getDefault().resolver(attr, value);
    }

    private Object doResolver(Object value) {
        if(value instanceof ScriptObjectMirror) {
            if(!((ScriptObjectMirror) value).isEmpty()) {
                if(((ScriptObjectMirror) value).isArray()) {
                    return ArrayUtil.toString(((ScriptObjectMirror) value).values());
                } else {
                    final JSONObject object = new JSONObject();
                    ((ScriptObjectMirror) value).entrySet().forEach(item -> object.put(item.getKey(), item.getValue()));
                    return object.toJSONString();
                }
            }
        }

        return value;
    }

    public String getKey() {
        return key;
    }
}
