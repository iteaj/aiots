package com.iteaj.iboot.module.iot.collect.websocket;

import com.iteaj.iot.server.websocket.WebSocketChannelMatcher;
import com.iteaj.iot.server.websocket.WebSocketServerListener;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerProtocol;

/**
 * 采集的数据实时监听
 */
public class CollectDataRealtimeListener implements WebSocketServerListener {

    private static final String URI = "/ws/iot/realtime";

    @Override
    public String uri() {
        return URI;
    }

    @Override
    public void onText(DefaultWebSocketServerProtocol protocol) {

    }

    /**
     * 写出数据
     * @param value
     */
    public static void write(Object value) {
        DefaultWebSocketServerProtocol.writeGroup(URI, "", new WebSocketChannelMatcher((channel, httpRequestWrapper) -> {

            return false;
        }));
    }
}
