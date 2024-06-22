package com.iteaj.iboot.module.iot.le5le;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iot.server.websocket.WebSocketServerListener;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerProtocol;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ScadaRealtimeDataListenerPush implements WebSocketServerListener, RealtimeInfoListener {

    private final static String URI = "/ws/iot/scada";

    @Override
    public String uri() {
        return URI;
    }

    @Override
    public void onText(DefaultWebSocketServerProtocol protocol) {
        for (int i = 0; i < 108; i++) {
            try {
                JSONObject data = new JSONObject();
                data.put("id", "23438-temp");
                data.put("value", RandomUtil.randomInt(100, 1000));
                DefaultWebSocketServerProtocol.writeGroup(URI, data.toJSONString());
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        if(value.getType() == "") {

        }
    }
}
