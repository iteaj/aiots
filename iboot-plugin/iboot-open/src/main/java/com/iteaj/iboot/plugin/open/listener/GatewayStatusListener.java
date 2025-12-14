package com.iteaj.iboot.plugin.open.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.iboot.plugin.open.entity.Northward;
import com.iteaj.iboot.plugin.open.service.INorthwardService;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.client.mqtt.gateway.MqttGatewayComponent;
import com.iteaj.iot.event.CombinedEventListener;
import org.springframework.beans.factory.annotation.Autowired;

public class GatewayStatusListener implements CombinedEventListener {

    @Autowired
    private INorthwardService northwardService;

    @Override
    public void online(String source, FrameworkComponent component) {
        if(component instanceof MqttGatewayComponent) {
            northwardService.update(Wrappers.<Northward>lambdaUpdate()
                    .set(Northward::getConnectStatus, "online")
                    .eq(Northward::getConnectKey,  source));
        }
    }

    @Override
    public void offline(String source, FrameworkComponent component) {
        if(component instanceof MqttGatewayComponent) {
            northwardService.update(Wrappers.<Northward>lambdaUpdate()
                    .set(Northward::getConnectStatus, "offline")
                    .eq(Northward::getConnectKey,  source));
        }
    }
}
