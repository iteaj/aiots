package com.iteaj.plugin.protocol.iec104.network;

import com.iteaj.iot.IotProtocolFactory;
import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.component.SimpleMultiClientManager;
import com.iteaj.iot.codec.filter.Interceptor;

public class IEC104Component extends SimpleMultiClientManager implements ClientComponent<IEC104Message> {

    private static long startTime = 0l;
    private static boolean start = false;
    private static final String NAME = "IEC-104";
    private static final String DESC = "电力行业IEC-104(60870)协议实现";

    public IEC104Component() {
        super();
    }

    @Override
    public IEC104ConnectProperties getConfig() {
        return null;
    }

    @Override
    public IotClient getClient() {
        return getClient(getConfig());
    }

    @Override
    public IotClient createNewClient(ClientConnectProperties config) {
        return new IEC104Client(this, (IEC104ConnectProperties) config);
    }

    public IEC104Client createNewClientAndConnect(ClientConnectProperties config, IEC104Call call) {
        final IEC104Client iec104Client = new IEC104Client(this, (IEC104ConnectProperties) config);
        iec104Client.init(call); iec104Client.connect();
        return iec104Client;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDesc() {
        return DESC;
    }

    @Override
    public boolean isStart() {
        return start;
    }

    @Override
    public long startTime() {
        return startTime;
    }

    @Override
    public synchronized void start(Object config) {
        this.startTime = System.currentTimeMillis();
        if(!isStart()) {
            start = true;
        }
    }

    @Override
    public synchronized void close() {
        if(this.isStart()) {
            getStorage().values().forEach(client -> client.close());
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
