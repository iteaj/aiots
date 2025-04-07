package com.iteaj.framework.spi.iot.listener.filter;

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

    public ScriptConfig() { }

    public ScriptConfig(String language, String script) {
        this.language = language;
        this.script = script;
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
}
