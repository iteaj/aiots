package com.iteaj.iboot.module.iot.utils;

import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ClientProtocolSupplier;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.NetworkConfig;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.TcpSocketClient;

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
            resolveGatewayProductClientStatus(client, device, status);
        }
    }

    private static NetworkConfig createClientConfig(DeviceProtocolSupplier supplier, Device device) {
        JSONObject config = device.getConfig();
        TransportProtocol type = supplier.getProtocol().getType();
        if(config == null) {
            throw new ServiceException("设备[" + device.getName() + " / " + device.getDeviceSn() + "]缺少配置");
        }

        config.put("deviceSn", device.getDeviceSn());
        return NetworkConfig.resolve(type, config);
    }

    private static void resolveGatewayProductClientStatus(IotClient client, Device device, FuncStatus status) {
        if(status == FuncStatus.enabled) {
            if(client instanceof TcpSocketClient) {
                if(!((TcpSocketClient) client).isConnect()) {
                    client.connect();
                }
            }
        } else if(status == FuncStatus.disabled) {
            if(client instanceof TcpSocketClient) {
                if(((TcpSocketClient) client).isConnect()) {
                    client.close(); // 断开连接并且移除客户端
                }
            }
        } else {
            throw new ServiceException("错误的状态[status="+status+"]");
        }
    }
}
