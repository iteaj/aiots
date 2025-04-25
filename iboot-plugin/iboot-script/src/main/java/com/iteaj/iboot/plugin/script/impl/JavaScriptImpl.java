package com.iteaj.iboot.plugin.script.impl;

import com.iteaj.framework.script.ScriptContext;
import com.iteaj.framework.script.ScriptExecException;
import com.iteaj.framework.script.ScriptInterface;
import com.iteaj.framework.script.ScriptLanguage;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptImpl implements ScriptInterface {

    private static ScriptEngineManager manager = new ScriptEngineManager();

    @Override
    public ScriptLanguage getLanguage() {
        return ScriptLanguage.javascript;
    }

    @Override
    public Object exec(ScriptContext context) {
        // 获取 JavaScript 脚本引擎
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            if(context.getParams() != null) {
                context.getParams().forEach((key, value) -> engine.put(key, value));
            }

            return engine.eval(context.getScript());
        } catch (ScriptException e) {
            throw new ScriptExecException("脚本执行异常[JavaScript]", e);
        }
    }
}
