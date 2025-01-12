package com.iteaj.iboot.plugin.protocol.dlt645.network.serial;

import com.iteaj.iot.IotProtocolFactory;
import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.codec.filter.Interceptor;
import com.iteaj.iot.serial.SerialClient;
import com.iteaj.iot.serial.SerialComponent;
import com.iteaj.iot.serial.SerialConnectProperties;

import java.util.List;

public class Dlt645SerialComponent implements ClientComponent<Dlt645SerialMessage> {

    private boolean start;
    private SerialComponent serialComponent;
    private static final String DESC = "基于串口+Dlt645协议客户端实现";

    public Dlt645SerialComponent() {
        serialComponent = SerialComponent.instance();
    }

    @Override
    public SerialConnectProperties getConfig() {
        return serialComponent.getConfig();
    }

    @Override
    public IotClient getClient() {
        return serialComponent.getClient();
    }

    @Override
    public void addClient(Object clientKey, IotClient value) {
        serialComponent.addClient(clientKey, value);
    }

    @Override
    public IotClient getClient(Object clientKey) {
        return serialComponent.getClient(clientKey);
    }

    @Override
    public IotClient removeClient(Object clientKey) {
        return serialComponent.removeClient(clientKey);
    }

    @Override
    public List<IotClient> clients() {
        return serialComponent.clients();
    }

    @Override
    public SerialClient createNewClient(ClientConnectProperties config) {
        return serialComponent.createNewClient(config);
    }

    @Override
    public String getName() {
        return "SerialDlt645";
    }

    @Override
    public String getDesc() {
        return DESC;
    }

    @Override
    public boolean isStart() {
        return this.start;
    }

    @Override
    public long startTime() {
        return this.serialComponent.startTime();
    }

    @Override
    public void start(Object config) {
        if(!isStart()) {
            this.start = true;
            this.serialComponent.start(config);
        }
    }

    @Override
    public void close() {
        if(isStart()) {
            this.start = false;
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
