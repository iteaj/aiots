package com.iteaj.framework.script;

import java.util.Arrays;
import java.util.List;

public interface LoggerMethodProvide {

    List<ScriptComplete> LOGGER_COMPLETES = Arrays.asList(
            new ScriptComplete("methods.console(msg)", "打印到控制台(支持{}占位符)",4),
            new ScriptComplete("methods.debug(uid, msg)", "打印调试日志(支持{}占位符)",4),
            new ScriptComplete("methods.info(uid, msg)", "打印信息日志(支持{}占位符)",4),
            new ScriptComplete("methods.warn(uid, msg)", "打印告警日志(支持{}占位符)",4),
            new ScriptComplete("methods.error(uid, msg)", "打印错误日志(支持{}占位符)",4)
    );

    default void console(Object msg) {
        System.out.println(msg);
    }


    default void debug(String uid, String msg, Object... params) {
        ScriptLoggerFilter.debug(getSubType(), uid, msg, params);
    }

    String getSubType();

    default void info(String uid, String msg, Object... params) {
        ScriptLoggerFilter.info(getSubType(), uid, msg, params);
    }

    default void warn(String uid, String msg, Object... params) {
        ScriptLoggerFilter.warn(getSubType(), uid, msg, params);
    }

    default void error(String uid, String msg, Object... params) {
        ScriptLoggerFilter.error(getSubType(), uid, msg, params);
    }
}
