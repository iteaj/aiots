package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.protocol.ProtocolSupplierException;
import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.ClientConnectProperties;
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

        String clientKey = getClientKey(config);
        K client = (K) component.getClient(clientKey);
        if(client != null) {
            return client;
        }

        client = doCreateClient(config, component);
        if(client != null) {
            client.init(config);
            if(component instanceof MultiClientManager) {
                component.addClient(clientKey, client);
            }
        }
        return client;
    }

    protected String getClientKey(C config) {
        return config.getDeviceSn();
    }

    @Override
    public synchronized K removeClient(Object clientKey) {
        T component = getComponent();
        if(component != null) {
            return (K) component.removeClient(clientKey);
        }

        return null;
    }

    protected abstract K doCreateClient(C config, T component);
}
