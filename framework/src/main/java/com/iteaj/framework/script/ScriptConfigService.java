package com.iteaj.framework.script;

import java.util.List;
import java.util.Objects;

/**
 * 执行脚本服务
 */
public interface ScriptConfigService {

    /**
     * 获取指定名称的脚本配置
     * @param key
     * @return
     */
    FunctionScript getScript(String key);

    List<FunctionScript> getScriptByType(String type);

    /**
     * 获取选中脚本配置
     * @param key
     * @return
     */
    default ScriptConfig getScriptConfig(String key) {
        final FunctionScript script = this.getScript(key);
        return getScriptConfig(script);
    }

    /**
     * 获取选中脚本配置
     * @param script
     * @return
     */
    default ScriptConfig getScriptConfig(FunctionScript script) {
        return (script != null && script.getLanguage() != null
                && !script.getLanguage().equals("disabled")
                && script.getConfigs() != null) ?
                script.getConfigs().stream()
                        .filter(item -> Objects.equals(item.getLanguage(), script.getLanguage()))
                        .findFirst().orElse(null) : null;
    }

    /**
     * 获取指定语言的脚本配置
     * @param key
     * @param language
     * @return
     */
    default ScriptConfig getScriptConfig(String key, String language) {
        final FunctionScript script = this.getScript(key);
        return script != null && script.getConfigs() != null ? script.getConfigs().stream()
                .filter(item -> item.getLanguage().equals(language))
                .findFirst().orElse(null) : null;
    }

    ScriptConfigService instance = new ScriptConfigServiceProxy();

    class ScriptConfigServiceProxy implements ScriptConfigService {

        private ScriptConfigService proxy;

        @Override
        public List<FunctionScript> getScriptByType(String type) {
            return proxy == null ? null : proxy.getScriptByType(type);
        }

        @Override
        public FunctionScript getScript(String key) {
            return proxy == null ? null : proxy.getScript(key);
        }

        public ScriptConfigService getProxy() {
            return proxy;
        }

        public void setProxy(ScriptConfigService proxy) {
            this.proxy = proxy;
        }
    }
}
