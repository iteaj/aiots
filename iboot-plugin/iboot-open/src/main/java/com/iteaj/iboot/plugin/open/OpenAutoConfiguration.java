package com.iteaj.iboot.plugin.open;

import com.iteaj.iboot.plugin.open.listener.MqttGatewayPublishListener;
import com.iteaj.iboot.plugin.open.service.INorthwardService;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayComponent;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.iteaj.iboot.plugin.open"})
@MapperScan({"com.iteaj.iboot.plugin.open.mapper"})
public class OpenAutoConfiguration {

    @Bean
    public MqttGatewayComponent mqttGatewayComponent() {
        return new MqttGatewayComponent();
    }

    @Bean
    public MqttGatewayPublishListener mqttGatewayPublishListener(INorthwardService northwardService) {
        return new MqttGatewayPublishListener(northwardService);
    }
}
