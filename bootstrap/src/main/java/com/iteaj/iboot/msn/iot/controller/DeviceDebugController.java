package com.iteaj.iboot.msn.iot.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.service.IDeviceService;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttComponent;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调试
 */
@RestController
@RequestMapping("/iot/debug")
public class DeviceDebugController extends BaseController {

    private final IDeviceService deviceService;
    private final DefaultMqttComponent component;

    public DeviceDebugController(DefaultMqttComponent component, IDeviceService deviceService) {
        this.component = component;
        this.deviceService = deviceService;
    }

    /**
     * 订阅
     * @param topic
     * @return
     */
    @GetMapping("mqtt/subscribe")
    public Result<Boolean> subscribe(String topic, MqttQoS qoS) {
        component.subscribe(topic, qoS);
        return success(true);
    }


}
