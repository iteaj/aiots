package com.iteaj.iboot.module.iot.le5le;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iot.DeviceManager;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.server.websocket.WebSocketServerListener;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerMessage;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerProtocol;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ScadaRealtimeDataListenerPush implements WebSocketServerListener, RealtimeInfoListener {

    private final static String URI = "/ws/iot/scada";
    private final static AttributeKey<Set<String>> REGISTER = AttributeKey.valueOf("POINT:REGISTER");

    @Override
    public String uri() {
        return URI;
    }

    @Override
    public void onText(DefaultWebSocketServerProtocol protocol) {
        String bindPoints = protocol.readText();
        Set<String> points = new HashSet<>();
        if(StrUtil.isNotBlank(bindPoints)) {
            points = Arrays.stream(bindPoints.split(",")).collect(Collectors.toSet());
        }

        DeviceManager deviceManager = FrameworkManager.getDeviceManager(DefaultWebSocketServerMessage.class);
        Channel channel = (Channel) deviceManager.find(protocol.getEquipCode());
        channel.attr(REGISTER).set(points);

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
        if(Objects.equals(value.getType(), "status")) {
            System.out.println(value);
        } else if(Objects.equals(value.getType(), "warn")) {

        } else if(Objects.equals(value.getType(), "data")) {

        }
    }
}
