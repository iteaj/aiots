package com.iteaj.framework.script;

import com.iteaj.framework.spi.iot.listener.filter.FunctionScript;
import com.iteaj.framework.spi.iot.listener.filter.ScriptConfig;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 执行脚本服务
 */
public interface ScriptConfigService {

    /**
     * 获取指定名称的脚本配置
     * @param name
     * @return
     */
    FunctionScript getScript(String name);

    /**
     * 获取选中脚本配置
     * @param name
     * @return
     */
    default ScriptConfig getScriptConfig(String name) {
        final FunctionScript script = this.getScript(name);
        return (script != null && script.getLanguage() != null && script.getConfigs() != null) ?
                script.getConfigs().stream()
                .filter(item -> Objects.equals(item.getLanguage(), script.getLanguage()))
                .findFirst().orElse(null) : null;
    }

    /**
     * 获取指定语言的脚本配置
     * @param name
     * @param language
     * @return
     */
    default ScriptConfig getScriptConfig(String name, String language) {
        final FunctionScript script = this.getScript(name);
        return script != null && script.getConfigs() != null ? script.getConfigs().stream()
                .filter(item -> item.getLanguage().equals(language))
                .findFirst().orElse(null) : null;
    }

    ScriptConfigService instance = new ScriptConfigServiceProxy();

    class ScriptConfigServiceProxy implements ScriptConfigService {

        private ScriptConfigService proxy;

        @Override
        public FunctionScript getScript(String name) {
            if(proxy == null) {
                return null;
            }

            return proxy.getScript(name);
        }

        public ScriptConfigService getProxy() {
            return proxy;
        }

        public void setProxy(ScriptConfigService proxy) {
            this.proxy = proxy;
        }
    }
}
