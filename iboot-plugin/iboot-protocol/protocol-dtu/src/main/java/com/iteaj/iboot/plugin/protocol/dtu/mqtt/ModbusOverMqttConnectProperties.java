package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.iot.client.mqtt.MqttConnectProperties;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;

import java.util.List;

public class ModbusOverMqttConnectProperties extends MqttConnectProperties {

    private String productCode;
    private String publishTopic;
    private String subscribeTopic;
    private final List<MqttTopicSubscription> topics;

    public ModbusOverMqttConnectProperties(String remoteHost, Integer remotePort
            , String clientId, List<MqttTopicSubscription> topics) {
        super(remoteHost, remotePort, clientId);
        this.topics = topics;
    }

    public List<MqttTopicSubscription> getTopics() {
        return topics;
    }

    public String getPublishTopic() {
        return publishTopic;
    }

    public void setPublishTopic(String publishTopic) {
        this.publishTopic = publishTopic;
    }

    public String getSubscribeTopic() {
        return subscribeTopic;
    }

    public void setSubscribeTopic(String subscribeTopic) {
        this.subscribeTopic = subscribeTopic;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
