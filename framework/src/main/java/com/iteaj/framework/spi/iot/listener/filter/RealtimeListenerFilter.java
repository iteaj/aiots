package com.iteaj.framework.spi.iot.listener.filter;

import com.iteaj.framework.spi.iot.listener.RealtimePayload;

/**
 * 实时数据监听的过滤
 */
public interface RealtimeListenerFilter extends ListenerScriptFilter<RealtimePayload> {

    @Override
    RealtimePayload filter(RealtimePayload param);
}
