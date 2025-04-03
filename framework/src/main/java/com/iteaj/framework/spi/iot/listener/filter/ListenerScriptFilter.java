package com.iteaj.framework.spi.iot.listener.filter;

import java.util.List;

public interface ListenerScriptFilter<T> extends ListenerEvalFilter<T>{

    /**
     * 支持的脚本语言
     * @return
     */
    List<EvalScript> getScripts();
}
