package com.iteaj.iboot.module.iot.le5le;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iot.CoreConst;
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

    private final static String URI = "/ws/iot/scada";
    private final DefaultWebSocketServerComponent webSocketServerComponent;
    private final IModelAttrRealtimeDataService modelAttrRealtimeDataService;
    private final static AttributeKey<Set<String>> REGISTER = AttributeKey.valueOf("POINT:REGISTER");

    public ScadaRealtimeDataListenerPush(DefaultWebSocketServerComponent webSocketServerComponent
            , IModelAttrRealtimeDataService modelAttrRealtimeDataService) {
        this.webSocketServerComponent = webSocketServerComponent;
        this.modelAttrRealtimeDataService = modelAttrRealtimeDataService;
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

        Channel channel = webSocketServerComponent.getDeviceManager().find(protocol.getEquipCode());
        channel.attr(REGISTER).set(points);

        JSONArray array = new JSONArray();
        points.forEach(key -> {
            String[] split = key.split(":");
            if(split[0].equals("s")) {
                modelAttrRealtimeDataService.getRealtimeData(split[1], split[2]);
            } else if(split[0].equals("w")) {

            } else if(split[0].equals("a")) {
                ModelAttrRealtimeData realtimeData = modelAttrRealtimeDataService.getRealtimeData(split[1], split[2]);
                if(realtimeData != null) {
                    SignalOrFieldValue value = realtimeData.getRealtime() != null ? realtimeData.getRealtime() : realtimeData.getPrev();
                    if(value != null && value.getValue() != null) {
                        Map<String, Object> itemValue = new HashMap<>();
                        itemValue.put("id", key);
                        itemValue.put("value", value.getValue());
                        array.add(itemValue);
                    }
                }
            }

        });
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

            if(!array.isEmpty()) {
                channels.forEach(channel -> {
                    if(channel.isActive()) { // 连接在线
                        Set<String> strings = channel.attr(REGISTER).get();
                        if(strings != null && !strings.isEmpty()) {
                            array.forEach(item -> {
                                JSONObject object = (JSONObject) item;
                                if(strings.contains(object.getString("id"))) {
                                    DefaultWebSocketServerProtocol.write(channel.attr(CoreConst.EQUIP_CODE).get(), object.toJSONString());
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
