package com.iteaj.framework.script;

public interface ScriptInterface {

    ScriptLanguage getLanguage();

    Object exec(ScriptContext context) throws ScriptExecException;
}
