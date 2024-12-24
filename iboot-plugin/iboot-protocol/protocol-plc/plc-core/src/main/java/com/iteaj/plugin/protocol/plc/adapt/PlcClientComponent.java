package com.iteaj.plugin.protocol.plc.adapt;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.iot.IotProtocolFactory;
import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.component.SimpleMultiClientManager;
import com.iteaj.iot.codec.filter.DecoderInterceptor;
import com.iteaj.iot.codec.filter.Interceptor;

import java.util.List;

public abstract class PlcClientComponent<M extends PlcClientMessage> extends SimpleMultiClientManager implements ClientComponent<M> {

    /**
     * 是否启用
     */
    private boolean start;

    /**
     * 开启时间
     */
    private long startTime;

    private PlcClientAdapter clientAdapter;

    private PlcConnectProperties properties;

    public PlcClientComponent() { }

    public PlcClientComponent(PlcConnectProperties properties) {
        this.properties = properties;
    }

    @Override
    public ClientConnectProperties getConfig() {
        return this.properties;
    }

    @Override
    public PlcClientAdapter getClient() {
        return clientAdapter;
    }

    @Override
    public abstract PlcClientAdapter createNewClient(ClientConnectProperties config);

    @Override
    public boolean isStart() {
        return this.start;
    }

    @Override
    public long startTime() {
        return this.startTime;
    }

    @Override
    public void start(Object config) {
        if(!this.isStart()) {
            synchronized (this) {
                this.start = true;
                this.startTime = System.currentTimeMillis();

                if(this.getConfig() != null) {
                    if(this.getClient() == null) {
                        this.clientAdapter = createNewClient(getConfig());
                        this.getClient().init(config);
                    }

                    this.getClient().connect();
                }
            }
        }
    }

    @Override
    public void close() {
        if(this.isStart()) {
            synchronized (this) {
                if(this.isStart()) {
                    this.start = false;
                    List<IotClient> clients = this.clients();
                    if(CollectionUtil.isNotEmpty(clients)) {
                        clients.forEach(item -> item.close());
                    }
                }
            }
        }
    }

    @Override
    public Interceptor getInterceptor() {
        return null;
    }

    @Override
    public IotProtocolFactory protocolFactory() {
        return null;
    }
}
