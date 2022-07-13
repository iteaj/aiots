package com.iteaj.iboot.msn.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.consts.DeviceStatus;
import com.iteaj.iboot.msn.iot.debug.tcp.server.TcpDebugServerComponent;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.service.IDeviceService;
import com.iteaj.iot.CoreConst;
import com.iteaj.iot.DeviceManager;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttComponent;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 调试
 */
@RestController
@RequestMapping("/iot/debug")
public class DeviceDebugController extends BaseController {

    private final IDeviceService deviceService;
    private final DefaultMqttComponent component;
    private final TcpDebugServerComponent tcpDebugServerComponent;

    public DeviceDebugController(@Autowired(required = false) DefaultMqttComponent component
            , @Autowired(required = false) TcpDebugServerComponent tcpDebugServerComponent
            , IDeviceService deviceService) {
        this.component = component;
        this.deviceService = deviceService;
        this.tcpDebugServerComponent = tcpDebugServerComponent;
    }

    @GetMapping("tcp")
    public Result<IPage<Device>> tcpList(Page page) {
        if(tcpDebugServerComponent != null) {
            DeviceManager deviceManager = tcpDebugServerComponent.getDeviceManager();
            page.setTotal(deviceManager.size()).setRecords(new ArrayList<>());

            deviceManager.forEach(item -> {
                InetSocketAddress address = (InetSocketAddress)item.remoteAddress();
                Device device = new Device()
                        .setIp(address.getHostName())
                        .setPort(address.getPort())
                        .setStatus(DeviceStatus.online)
                        .setName(item.id().asShortText());
                Object o = item.attr(CoreConst.EQUIP_CODE).get();
                if(o != null) {
                    device.setDeviceSn(o.toString());
                }

                page.getRecords().add(device);
            });

            return success(page);
        } else {
            return success();
        }
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
