package com.iteaj.iboot.plugin.script.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.script.*;
import com.iteaj.framework.script.ScriptContext;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

public class JavaScriptImpl implements ScriptInterface<CompiledScript> {

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    @Override
    public ScriptLanguage getLanguage() {
        return ScriptLanguage.javascript;
    }

    @Override
    public CompiledScript compile(String script) {
        try {
            return ((Compilable) engine).compile(script);
        } catch (ScriptException e) {
            throw new ScriptExecException("编译失败["+e.getMessage()+"]", e);
        }
    }

    @Override
    public Object exec(String script, Map<String, Object> params) {
        try {
            final Bindings bindings = engine.createBindings();
            if(CollectionUtil.isNotEmpty(params)) {
                bindings.putAll(params);
            }

            return engine.eval(script);
        } catch (ScriptException e) {
            throw new ScriptExecException("脚本执行异常[JavaScript]", e);
        }
    }

    @Override
    public Object exec(ScriptContext context) {
        // 获取 JavaScript 脚本引擎
        try {
            final Bindings bindings = engine.createBindings();
            if(context.getParams() != null) {
                bindings.putAll(context.getParams());
            }

            CompiledScript compiledScript = (CompiledScript) ScriptManager.getCompilable(context.getKey());
            if(compiledScript != null) {
                return compiledScript.eval(bindings);
            }

            return engine.eval(context.getScript(), bindings);
        } catch (ScriptException e) {
            throw new ScriptExecException("脚本执行异常[JavaScript] "+e.getMessage(), e);
        }
    }
}
