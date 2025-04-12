package com.iteaj.iboot.plugin.script.model;

import com.iteaj.framework.script.ScriptContext;
import com.iteaj.framework.script.ScriptInterface;
import com.iteaj.framework.script.ScriptManager;
import com.iteaj.framework.spi.iot.DataValueResolver;
import com.iteaj.framework.spi.iot.UpModelAttr;
import com.iteaj.framework.spi.iot.listener.filter.ListenerEvalFilter;
import com.iteaj.framework.spi.iot.listener.filter.ListenerScriptFilter;
import com.iteaj.framework.spi.iot.listener.filter.ScriptConfig;

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
            final Object exec = scriptInterface.exec(new ScriptContext(script.getScript())
                    .addParam("value", value).addParam("model", attr)
                    .addParam("console", ListenerScriptFilter.LOGGER_PROVIDE));

            return exec;
        }

        return value;
    }
}
