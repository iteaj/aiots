package com.iteaj.iboot.msn.iot.mqtt;

import com.iteaj.iot.client.mqtt.impl.DefaultMqttSubscribeProtocol;
import com.iteaj.iot.client.mqtt.impl.MqttSubscribeListener;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import org.springframework.stereotype.Component;

//@Component
public class MqttDebugSubscribeListener implements MqttSubscribeListener {

    private MqttTopicSubscription subscription = new MqttTopicSubscription("iot/debug/#", MqttQoS.AT_MOST_ONCE);

    @Override
    public MqttTopicSubscription topic() {
        return subscription;
    }

    @Override
    public void onSubscribe(DefaultMqttSubscribeProtocol protocol) {

    }
}
