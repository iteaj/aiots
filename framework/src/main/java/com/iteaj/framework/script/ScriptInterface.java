package com.iteaj.framework.script;

import java.util.Map;

public interface ScriptInterface<T> {

    ScriptLanguage getLanguage();

    /**
     * 编译
     * @param script
     * @return
     */
    T compile(String script);

    Object exec(String script, Map<String, Object> params);

    Object exec(ScriptContext context) throws ScriptExecException;
}
