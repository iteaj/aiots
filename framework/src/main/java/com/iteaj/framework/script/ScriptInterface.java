package com.iteaj.framework.script;

import com.iteaj.framework.spi.iot.listener.filter.ScriptLanguage;

public interface ScriptInterface {

    ScriptLanguage getLanguage();

    Object exec(ScriptContext context) throws ScriptExecException;
}
