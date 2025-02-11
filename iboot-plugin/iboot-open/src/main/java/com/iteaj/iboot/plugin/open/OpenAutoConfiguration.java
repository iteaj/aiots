package com.iteaj.iboot.plugin.open;

import com.iteaj.iot.client.mqtt.gateway.MqttGatewayComponent;
import org.springframework.context.annotation.Bean;

public class OpenAutoConfiguration {

    @Bean
    public MqttGatewayComponent mqttGatewayComponent() {
        return new MqttGatewayComponent();
    }
}
