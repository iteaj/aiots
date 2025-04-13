package com.iteaj.iboot.plugin.open.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.listener.IotAsyncListener;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.framework.spi.iot.listener.filter.RealtimeListenerFilter;
import com.iteaj.iboot.plugin.open.entity.Northward;
import com.iteaj.iboot.plugin.open.service.INorthwardService;
import com.iteaj.iot.client.mqtt.MqttClient;
import com.iteaj.iot.client.mqtt.gateway.*;
import com.iteaj.iot.message.DefaultMessageBody;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class MqttGatewayPublishListener implements IotAsyncListener<RealtimePayload>, RealtimeListenerFilter {

    private final INorthwardService northwardService;
    private final MqttGatewayComponent mqttGatewayComponent;

    public MqttGatewayPublishListener(INorthwardService northwardService
            , MqttGatewayComponent mqttGatewayComponent) {
        this.northwardService = northwardService;
        this.mqttGatewayComponent = mqttGatewayComponent;
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return IotEventType.Realtime == eventType;
    }

    @Override
    public void doIotEvent(IotEventType eventType, RealtimePayload value) {
        final String type = value.getType();
        if(isFilter(type, value)) { // 实时推送
            List<Northward> northwards = northwardService.listByType("mqtt_publish");
            if(CollectionUtil.isNotEmpty(northwards)) {

                final MqttGatewayHead gatewayHead = new MqttGatewayHead("MqttGatewayPublishListener");
                final byte[] message = createMqttBodyMessage(type, value.getPayload());
                final DefaultMessageBody messageBody = new DefaultMessageBody(message);

                northwards.forEach(item -> {
                    if(Boolean.TRUE.equals(item.getStatus())) {
                        final JSONObject config = item.getConfig();
                        MqttGatewayConnectProperties connectProperties = getClientProperties(config);
                        final MqttGatewayMessage gatewayMessage = new MqttGatewayMessage(gatewayHead, messageBody
                                , connectProperties.getQoS(), connectProperties.getTopic());
                        new MqttGatewayProtocol(gatewayMessage).request(connectProperties);
                    }
                });
            }
        }
    }

    private byte[] createMqttBodyMessage(String type, Collection payload) {
        JSONObject body = new JSONObject();
        body.put("type", type);
        if(type.equals("status")) {
            body.put("data", createStatusData(payload));
        } else {
            body.put("data", createRealtimeData(payload, body));
        }

        return body.toJSONString().getBytes(StandardCharsets.UTF_8);
    }

    private JSONArray createRealtimeData(Collection payload, JSONObject body) {
        JSONArray array = new JSONArray();
        payload.forEach(item -> {
            final RealtimeCollectData data = (RealtimeCollectData) item;
            final JSONObject dataItem = new JSONObject();
            body.put("uid", data.getUid());
            body.put("deviceName", data.getDeviceName());
            body.put("collectTime", data.getCollectTime());

            dataItem.put("cid", data.getCid());
            dataItem.put("field", data.getField());
            dataItem.put("value", data.getValue());
            dataItem.put("status", data.getStatus());
            dataItem.put("reason", data.getReason());
            dataItem.put("address", data.getAddress());
            dataItem.put("fieldName", data.getFieldName());
            array.add(dataItem);
        });

        return array;
    }

    private JSONArray createStatusData(Collection payload) {
        JSONArray array = new JSONArray();
        payload.forEach(item -> {
            final RealtimeDeviceStatus data = (RealtimeDeviceStatus) item;
            final JSONObject dataItem = new JSONObject();
            dataItem.put("uid", data.getUid());
            dataItem.put("type", data.getDeviceType());
            dataItem.put("deviceSn", data.getDeviceSn());
            dataItem.put("deviceName", data.getDeviceName());
            dataItem.put("collectTime", data.getUpdateTime());
            dataItem.put("parentDeviceSn", data.getParentDeviceSn());
            array.add(dataItem);
        });

        return array;
    }

    private MqttGatewayConnectProperties getClientProperties(JSONObject config) {
        final String clientId = config.getString("clientId");
        final MqttClient client = mqttGatewayComponent.getClient(clientId);
        if(client == null) {
            final String host = config.getString("host");
            final Integer port = config.getInteger("port");
            final String topic = config.getString("topic");
            return new MqttGatewayConnectProperties(host, port, clientId, topic);
        } else {
            return (MqttGatewayConnectProperties) client.getConfig();
        }
    }

    @Override
    public String getFilterName() {
        return FILTER_MQTT_FORWARD;
    }

    @Override
    public boolean isFilter(String type, RealtimePayload payload) {
        return "data".equals(type) || "status".equals(type);
    }
}
