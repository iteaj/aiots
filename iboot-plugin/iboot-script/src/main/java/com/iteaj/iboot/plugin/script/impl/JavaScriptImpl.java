package com.iteaj.iboot.plugin.script.impl;

import com.iteaj.framework.script.*;
import com.iteaj.framework.script.ScriptContext;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

public class JavaScriptImpl implements ScriptInterface<CompiledScript> {

    private Map<String, CompiledScript> scriptMap = new HashMap<>();
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    @Override
    public ScriptLanguage getLanguage() {
        return ScriptLanguage.javascript;
    }

    @Override
    public CompiledScript compile(String script) {
        try {
            return ((Compilable) engine).compile(script);
        } catch (ScriptException e) {
            throw new ScriptExecException("编译失败", e);
        }
    }

    @Override
    public Object exec(ScriptContext context) {
        // 获取 JavaScript 脚本引擎
        try {
            final Bindings bindings = engine.createBindings();
            if(context.getParams() != null) {
                context.getParams().forEach((key, value) -> bindings.put(key, value));
            }

            CompiledScript compiledScript = (CompiledScript) ScriptManager.getCompilable(context.getKey());
            if(compiledScript == null) {
                synchronized (this) {
                    compiledScript = (CompiledScript) ScriptManager.getCompilable(context.getKey());
                    if(compiledScript == null) {
                        ScriptManager.register(context.getKey(), compiledScript = this.compile(context.getScript()));
                    }
                }
            }

            return compiledScript.eval(bindings);
        } catch (ScriptException e) {
            throw new ScriptExecException("脚本执行异常[JavaScript]", e);
        }
    }
}
