package com.iteaj.iboot.plugin.open.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.listener.IotAsyncListener;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.plugin.open.entity.Northward;
import com.iteaj.iboot.plugin.open.service.INorthwardService;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayHead;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayMessage;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayProtocol;
import com.iteaj.iot.message.DefaultMessageBody;

import java.util.List;

public class MqttGatewayPublishListener implements IotAsyncListener<RealtimePayload> {

    private final INorthwardService northwardService;

    public MqttGatewayPublishListener(INorthwardService northwardService) {
        this.northwardService = northwardService;
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return IotEventType.Realtime == eventType;
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        final String type = value.getType();
        if("data".equals(type)) { // 实时推送
            List<Northward> northwards = northwardService.listByType("mqtt");
            if(CollectionUtil.isNotEmpty(northwards)) {
                final MqttGatewayHead gatewayHead = new MqttGatewayHead("");
                final DefaultMessageBody messageBody = new DefaultMessageBody();
                final MqttGatewayMessage gatewayMessage = new MqttGatewayMessage(gatewayHead, messageBody, null, null);

                northwards.forEach(item -> {
                    new MqttGatewayProtocol(gatewayMessage).request();
                });
            }
        }
    }
}
