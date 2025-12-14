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
import io.netty.handler.codec.mqtt.MqttQoS;

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
        if(isFilter(type, value) && CollectionUtil.isNotEmpty(value.getPayload())) { // 实时推送
            List<Northward> northwards = northwardService.listByType("mqtt_publish");
            if(CollectionUtil.isNotEmpty(northwards)) {

                final MqttGatewayHead gatewayHead = new MqttGatewayHead("MqttGatewayPublishListener");
                final byte[] message = createMqttBodyMessage(value);
                final DefaultMessageBody messageBody = new DefaultMessageBody(message);

                northwards.forEach(item -> {
                    if(Boolean.TRUE.equals(item.getStatus())) {
                        MqttGatewayConnectProperties connectProperties = getClientProperties(item);
                        final MqttGatewayMessage gatewayMessage = new MqttGatewayMessage(gatewayHead, messageBody
                                , connectProperties.getQoS(), connectProperties.getTopic());
                        new MqttGatewayProtocol(gatewayMessage).request(connectProperties);
                    }
                });
            }
        }
    }

    private byte[] createMqttBodyMessage(RealtimePayload payload) {
        JSONObject body = new JSONObject();
        body.put("type", payload.getType());
        if(payload.isStatus()) {
            body.put("data", createStatusData(payload.getPayload()));
        } else {
            body.put("data", createRealtimeData(payload.getPayload(), body));
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

    private MqttGatewayConnectProperties getClientProperties(Northward northward) {
        final MqttClient client = mqttGatewayComponent.getClient(northward.getConnectKey());
        if(client == null) {
            JSONObject config = northward.getConfig();
            final String qos = config.getString("qos");
            final String host = config.getString("host");
            final Integer port = config.getInteger("port");
            final String topic = config.getString("topic");
            final String clientId = config.getString("clientId");
            MqttGatewayConnectProperties properties = new MqttGatewayConnectProperties(host, port, clientId, topic, MqttQoS.valueOf(Integer.parseInt(qos)));
            properties.setUsername(config.getString("username"));
            properties.setPassword(config.getString("password"));
            properties.setConnectKey(northward.getConnectKey());
            return properties;
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
        return payload.isData() || payload.isStatus() || payload.isDataActive();
    }
}
