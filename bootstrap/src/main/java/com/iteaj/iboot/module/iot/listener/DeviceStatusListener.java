package com.iteaj.iboot.module.iot.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.SocketClient;
import com.iteaj.iot.event.ClientStatusEventListener;
import com.iteaj.iot.server.websocket.impl.DefaultWebSocketServerComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 设备上下线状态事件监听
 */
@Component
public class DeviceStatusListener implements ClientStatusEventListener, DisposableBean {

    private final IDeviceService deviceService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public DeviceStatusListener(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void online(Object source, FrameworkComponent component) {
        // 服务端设备上线事件
        if(source instanceof String) {
            if(!(component instanceof DefaultWebSocketServerComponent)) {
                deviceService.update((String) source, DeviceStatus.online);
            }
        } else if(source instanceof SocketClient){ // 客户端连接成功事件
            ClientConnectProperties config = ((SocketClient) source).getConfig();
            deviceService.update(config.connectKey(), DeviceStatus.online);
        }
    }

    @Override
    public void offline(Object source, FrameworkComponent component) {
        // 服务端设备掉线事件
        if(source instanceof String) {
            deviceService.update((String) source, DeviceStatus.offline);
        } else if(source instanceof SocketClient) {// 客户端连接断开事件
            ClientConnectProperties config = ((SocketClient) source).getConfig();
            deviceService.update(config.connectKey(), DeviceStatus.offline);
        }
    }

    @Override
    public void destroy() throws Exception {
        // 程序关闭时 更新所有的设备状态到离线
        deviceService.update(Wrappers.<Device>lambdaUpdate()
                .set(Device::getStatus, DeviceStatus.offline)
                .set(Device::getSwitchTime, new Date()));

        if(logger.isDebugEnabled()) {
            logger.debug("程序关闭... 更新所有设备状态到[offline]");
        }
    }
}
