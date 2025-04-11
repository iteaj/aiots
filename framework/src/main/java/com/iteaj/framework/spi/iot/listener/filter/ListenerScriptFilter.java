package com.iteaj.framework.spi.iot.listener.filter;

import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.spi.iot.listener.IotEventPayload;

import java.util.List;

public interface ListenerScriptFilter<T extends IotEventPayload> extends ListenerEvalFilter<T>{

    LoggerMethodProvide LOGGER_PROVIDE = new LoggerMethodProvide();

    /**
     * 返回脚本备注
     * @return
     */
    String getScriptRemark();

    /**
     * 支持的脚本语言
     * @return
     */
    List<ScriptConfig> getScripts();

    /**
     * 自动补全内容
     * @return
     */
    List<ScriptComplete> getCompletes();
}
