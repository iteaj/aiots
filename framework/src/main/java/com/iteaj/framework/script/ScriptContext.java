package com.iteaj.framework.script;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScriptContext {

    private String key;

    private String script;

    private String invoke;

    private Map<String, Object> params;

    protected ScriptContext(String key, String script, String invoke) {
        this.key = key;
        this.invoke = invoke;
        this.script = script;
    }

    public static ScriptContext build(String key, String script, String invoke) {
        return new ScriptContext(key, script, invoke);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getInvoke() {
        return invoke;
    }

    public void setInvoke(String invoke) {
        this.invoke = invoke;
    }

    public ScriptContext addParam(String name, Object value) {
        if(this.params == null) {
            this.params = new LinkedHashMap<>();
        }

        this.getParams().put(name, value);
        return this;
    }
}
