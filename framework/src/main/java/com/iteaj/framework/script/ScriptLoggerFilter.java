package com.iteaj.framework.script;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.logger.LoggerFilter;
import com.iteaj.framework.logger.PushParams;
import com.iteaj.framework.spi.iot.DeviceLogger;
import com.iteaj.framework.spi.iot.listener.filter.RealtimeMethodProvide;

import java.util.Arrays;
import java.util.List;

public class ScriptLoggerFilter implements LoggerFilter {

    private static final String TYPE = "script";
    private static final String NAME = "脚本日志";

    public final List<IVOption> subOptions = Arrays.asList(
            new IVOption("脚本联动", "脚本联动"),
            new IVOption(ValueConvertProvide.NAME, ValueConvertProvide.NAME),
            new IVOption(RealtimeMethodProvide.NAME, RealtimeMethodProvide.NAME),
            new IVOption(ScriptCmdMethodsProvider.NAME, ScriptCmdMethodsProvider.NAME),
            new IVOption(CalcAttrScriptMethodProvide.NAME, CalcAttrScriptMethodProvide.NAME)
    );

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean filter(PushParams params) {
        return params.getType().equals(NAME);
    }

    @Override
    public List<IVOption> subOptions() {
        return subOptions;
    }

    public static void debug(String subType, String uid, String msg, Object... args) {
        DeviceLogger.debug(NAME, subType, uid, msg, args);
    }

    public static void info(String subType, String uid, String msg, Object... args) {
        DeviceLogger.info(NAME, subType, uid, msg, args);
    }

    public static void warn(String subType, String uid, String msg, Object... args) {
        DeviceLogger.warn(NAME, subType, uid, msg, args);
    }

    public static void error(String subType, String uid, String msg, Object... args) {
        DeviceLogger.error(NAME, subType, uid, msg, args);
    }
}
