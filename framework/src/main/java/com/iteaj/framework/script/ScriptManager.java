package com.iteaj.framework.script;

import java.util.HashMap;
import java.util.Map;

public class ScriptManager {

    private static final Map<String, ScriptInterface> interfaceMap = new HashMap<>(8);

    public static void register(ScriptInterface scriptInterface) {
        interfaceMap.putIfAbsent(scriptInterface.getLanguage().getName(), scriptInterface);
    }

    public static Object exec(String language, ScriptContext context) {
        final ScriptInterface scriptInterface = interfaceMap.get(language);
        return scriptInterface != null ? scriptInterface.exec(context) : null;
    }

    public static ScriptInterface get(String language) {
        return interfaceMap.get(language);
    }

    public static ScriptInterface remove(String language) {
        return interfaceMap.remove(language);
    }

    public static boolean isExists(String language) {
        return interfaceMap.containsKey(language);
    }
}
