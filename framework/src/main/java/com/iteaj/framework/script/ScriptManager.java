package com.iteaj.framework.script;

import com.iteaj.framework.logger.LoggerManager;

import java.util.HashMap;
import java.util.Map;

public class ScriptManager {

    private static final Map<String, Object> compiledScriptMap = new HashMap<>();
    private static final Map<String, ScriptInterface> interfaceMap = new HashMap<>(8);

    static {
        LoggerManager.addFilter(new ScriptLoggerFilter());
    }

    /**
     * 注册脚本引擎
     * @param scriptInterface
     */
    public static void register(ScriptInterface scriptInterface) {
        interfaceMap.putIfAbsent(scriptInterface.getLanguage().getName(), scriptInterface);
    }

    /**
     * 注册或者覆盖已注册编译的脚本
     * @param key
     * @param compilable
     */
    public static void register(String key, Object compilable) {
        compiledScriptMap.put(key, compilable);
    }

    public static void register(String key, ScriptConfig script) {
        final ScriptInterface scriptInterface = get(script.getLanguage());
        if(scriptInterface != null) {
            final Object compile = scriptInterface.compile(script.getScript() + "\r\n" + script.getInvoke());
            if(compile != null) {
                register(key, compile);
            }
        }
    }

    public static Object exec(String language, ScriptContext context) {
        final ScriptInterface scriptInterface = interfaceMap.get(language);
        return scriptInterface != null ? scriptInterface.exec(context) : null;
    }

    public static Object exec(String language, String script, Map<String, Object> params) {
        final ScriptInterface scriptInterface = interfaceMap.get(language);
        return scriptInterface != null ? scriptInterface.exec(script, params) : null;
    }

    public static ScriptInterface get(String language) {
        return interfaceMap.get(language);
    }

    public static Object getCompilable(String key) {
        return compiledScriptMap.get(key);
    }

    public static ScriptInterface remove(String language) {
        return interfaceMap.remove(language);
    }

    public static Object removeCompilable(String key) {
        return compiledScriptMap.remove(key);
    }

    public static boolean isExists(String language) {
        return interfaceMap.containsKey(language);
    }
}
