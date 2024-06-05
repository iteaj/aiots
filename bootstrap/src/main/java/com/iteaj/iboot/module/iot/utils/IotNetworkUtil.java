package com.iteaj.iboot.module.iot.utils;

import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ClientProtocolSupplier;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.NetworkConfig;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.TcpSocketClient;
import com.iteaj.iot.event.ClientStatus;
import com.iteaj.iot.event.IotEvent;
import com.iteaj.iot.event.StatusEvent;

import java.lang.reflect.Type;

public class IotNetworkUtil {

    /**
     * 网络控制
     * @param supplier
     * @param device
     * @param status 连接还是断开
     */
    public static void networkCtrl(DeviceProtocolSupplier supplier, Device device, FuncStatus status) {
        IotClient client = null;
        NetworkConfig networkConfig = createClientConfig(supplier, device);
        try {
            client = ((ClientProtocolSupplier) supplier).createClient(networkConfig);
        } finally {
            // 处理网关产品设备的状态
            resolveGatewayProductClientStatus(client, device, status, supplier.getComponent());
        }
    }

    private static NetworkConfig createClientConfig(DeviceProtocolSupplier supplier, Device device) {
        JSONObject config = device.getConfig();
        if(config == null) {
            throw new ServiceException("设备[" + device.getName() + " / " + device.getDeviceSn() + "]缺少配置");
        }

        config.put("deviceSn", device.getDeviceSn());
        return config.toJavaObject((Type) supplier.getNetworkConfigClazz());
    }

    private static void resolveGatewayProductClientStatus(IotClient client, Device device, FuncStatus status, FrameworkComponent component) {
        if(status == FuncStatus.enabled) {
            if(client instanceof TcpSocketClient) {
                if(!((TcpSocketClient) client).isConnect()) {
                    client.connect();
                }
            } else {
                Object connect = client.connect();
                if(connect instanceof Boolean) {
                    if((boolean) connect) {
                        FrameworkManager.publishEvent(new StatusEvent(client, ClientStatus.online, component));
                    }
                }
            }
        } else if(status == FuncStatus.disabled) {
            if(client instanceof TcpSocketClient) {
                if(((TcpSocketClient) client).isConnect()) {
                    client.close(); // 断开连接并且移除客户端
                } else {// 如果客户端不在线直接移除客户端
                    client.getClientComponent().removeClient(client.getConfig());
                }
            } else {
                Object close = client.close();
                if(close instanceof Boolean) {
                    if((Boolean) close) {
                        FrameworkManager.publishEvent(new StatusEvent(client, ClientStatus.offline, component));
                    }
                }
            }
        } else {
            throw new ServiceException("错误的状态[status="+status+"]");
        }
    }
}
