package com.iteaj.iboot.module.iot.le5le;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.data.WarnData;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iot.CoreConst;
import com.iteaj.iot.server.manager.TcpDeviceManager;
import com.iteaj.iot.server.websocket.WebSocketServerListener;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerComponent;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerProtocol;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ScadaRealtimeDataListenerPush implements WebSocketServerListener, RealtimeInfoListener {

    private TcpDeviceManager deviceManager;
    private final static String URI = "/ws/iot/scada";
    private final DefaultWebSocketServerComponent webSocketServerComponent;
    private final static AttributeKey<Set<String>> REGISTER = AttributeKey.valueOf("POINT:REGISTER");

    public ScadaRealtimeDataListenerPush(DefaultWebSocketServerComponent webSocketServerComponent) {
        this.webSocketServerComponent = webSocketServerComponent;
    }

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

        if(deviceManager == null) {
            deviceManager = webSocketServerComponent.getDeviceManager();
        }

        Channel channel = deviceManager.find(protocol.getEquipCode());
        channel.attr(REGISTER).set(points);
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        // 有客户端连接存在
        webSocketServerComponent.group(URI).ifPresent(channels -> {
            JSONArray array = new JSONArray();
            if(Objects.equals(value.getType(), "status")) {
                value.getPayload().forEach(status -> {
                    JSONObject jsonObject = new JSONObject();
                    RealtimeDeviceStatus deviceStatus = (RealtimeDeviceStatus) status;
                    jsonObject.put("id", "s:"+deviceStatus.getUid());
                    jsonObject.put("value", deviceStatus.getDeviceStatus());
                    array.add(jsonObject);
                });
            } else if(Objects.equals(value.getType(), "warn")) {
                value.getPayload().forEach(item -> {
                    JSONObject jsonObject = new JSONObject();
                    WarnData warn = (WarnData) item;
                    jsonObject.put("id", "w:"+warn.getUid()+":"+warn.getField());
                    jsonObject.put("value", warn.getValue());
                    array.add(jsonObject);
                });
            } else if(Objects.equals(value.getType(), "data")) {
                value.getPayload().forEach(item -> {
                    JSONObject jsonObject = new JSONObject();
                    RealtimeCollectData data = (RealtimeCollectData) item;
                    jsonObject.put("id", "a:"+data.getUid()+":"+data.getField());
                    jsonObject.put("value", data.getValue());
                    array.add(jsonObject);
                });
            }

            if(array.size() > 0) {
                channels.forEach(channel -> {
                    if(channel.isActive()) { // 连接在线
                        Set<String> strings = channel.attr(REGISTER).get();
                        if(strings != null && strings.size() > 0) {
                            if(array.size() == 1) {
                                // 只有一条记录, 直接写出
                                array.forEach(item -> {
                                    JSONObject object = (JSONObject) item;
                                    if(strings.contains(object.getString("id"))) {
                                        DefaultWebSocketServerProtocol.write(channel.attr(CoreConst.EQUIP_CODE).get(), object.toJSONString());
                                    }
                                });
                            } else { // 有多条记录
                                List<JSONObject> result = new ArrayList<>();
                                array.forEach(item -> {
                                    JSONObject object = (JSONObject) item;
                                    if(strings.contains(object.getString("id"))) {
                                        result.add(object);
                                    }
                                });

                                // 有数据需要写出
                                if(result.size() > 0) {
                                    DefaultWebSocketServerProtocol.write(channel.attr(CoreConst.EQUIP_CODE).get(), JSON.toJSONString(result));
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}
