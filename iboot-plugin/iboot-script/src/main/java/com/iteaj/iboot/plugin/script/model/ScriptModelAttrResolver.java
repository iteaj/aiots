package com.iteaj.iboot.plugin.script.model;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.script.ScriptContext;
import com.iteaj.framework.script.ScriptInterface;
import com.iteaj.framework.script.ScriptManager;
import com.iteaj.framework.spi.iot.DataValueResolver;
import com.iteaj.framework.spi.iot.DataValueResolverFactory;
import com.iteaj.framework.spi.iot.UpModelAttr;
import com.iteaj.framework.spi.iot.listener.filter.ListenerScriptFilter;
import com.iteaj.framework.spi.iot.listener.filter.RealtimeListenerFilter;
import com.iteaj.framework.script.ScriptConfig;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * 模型属性的脚本解析
 */
public class ScriptModelAttrResolver implements DataValueResolver {

    private final String name;
    private final ScriptConfig script;
    private ScriptInterface scriptInterface;

    public ScriptModelAttrResolver(String name, ScriptConfig script) {
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
            final Object exec = scriptInterface.exec(ScriptContext.build(script.getScript(), "resolver(value, model, methods);")
                    .addParam("value", value).addParam("model", attr)
                    .addParam("methods", RealtimeListenerFilter.convertProvide));

            return doResolver(exec);
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
}
