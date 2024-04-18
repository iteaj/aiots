package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.protocol.ProtocolSupplierException;
import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.MultiClientManager;

public abstract class StaticClientProtocolSupplier<C extends NetworkConfigImpl, T extends ClientComponent, K extends IotClient>
        extends StaticProtocolSupplier<C, T> implements ClientProtocolSupplier<C, T, K>{

    @Override
    public synchronized K createClient(C config) {
        T component = getComponent();
        if(component == null || !component.isStart()) {
            throw new ProtocolSupplierException("未启动网络组件");
        }

        K client = (K) component.getClient(config.getDeviceSn());
        if(client != null) {
            // 防止网关设备修改了host和ip
            client.getConfig().setHost(config.getHost());
            client.getConfig().setPort(config.getPort());
            return client;
        }

        client = doCreateClient(config, component);
        if(client != null) {
            client.init(null);
            if(component instanceof MultiClientManager) {
                component.addClient(config.getDeviceSn(), client);
            }
        }
        return client;
    }

    protected abstract K doCreateClient(C config, T component);
}
