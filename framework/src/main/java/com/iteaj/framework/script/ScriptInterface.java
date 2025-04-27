package com.iteaj.framework.script;

public interface ScriptInterface<T> {

    ScriptLanguage getLanguage();

    /**
     * 编译
     * @param script
     * @return
     */
    T compile(String script);

    Object exec(ScriptContext context) throws ScriptExecException;
}
