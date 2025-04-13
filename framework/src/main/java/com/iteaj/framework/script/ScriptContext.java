package com.iteaj.framework.script;

import java.util.HashMap;
import java.util.Map;

public class ScriptContext {

    private String script;

    private Map<String, Object> params;

    public ScriptContext() { }

    public ScriptContext(String script) {
        this.script = script;
    }

    public static ScriptContext build(String script, String invoke) {
        return new ScriptContext(script + "\r\n" + invoke);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public ScriptContext addParam(String name, Object value) {
        if(this.params == null) {
            this.params = new HashMap<>();
        }

        this.getParams().put(name, value);
        return this;
    }
}
