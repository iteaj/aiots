package com.iteaj.framework.script;

/**
 * 脚本配置
 */
public class ScriptConfig {

    /**
     * 脚本语言
     */
    private String language;

    /**
     * 脚本示例
     */
    private String script;

    /**
     * 执行方法
     */
    private String invoke;

    public ScriptConfig() { }

    public ScriptConfig(String language, String invoke, String script) {
        this.language = language;
        this.script = script;
        this.invoke = invoke;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getInvoke() {
        return invoke;
    }

    public void setInvoke(String invoke) {
        this.invoke = invoke;
    }
}
