package com.iteaj.plugin.protocol.iec104.network;

import com.iteaj.iot.client.ClientConnectProperties;
import org.openmuc.j60870.ClientConnectionBuilder;

import java.net.UnknownHostException;

public class IEC104ConnectProperties extends ClientConnectProperties {

    /**
     * 主站地址
     */
    private int originatorAddress;
    private ClientConnectionBuilder connectionBuilder;
    public static final String CONNECT_PREFIX = "IEC104";

    public IEC104ConnectProperties() {
        this("127.0.0.1", 2404);
    }

    public IEC104ConnectProperties(String remoteHost, Integer remotePort) {
        this(remoteHost, remotePort, String.format("%s://%s:%d", CONNECT_PREFIX, remoteHost, remotePort));
    }

    public IEC104ConnectProperties(String remoteHost, Integer remotePort, String connectKey) {
        super(remoteHost, remotePort, connectKey);
        try {
            connectionBuilder = new ClientConnectionBuilder(remoteHost).setPort(remotePort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ClientConnectProperties setConnectTimeout(long connectTimeout) {
        connectionBuilder.setConnectionTimeout((int) connectTimeout);
        return super.setConnectTimeout(connectTimeout);
    }

    public ClientConnectionBuilder getConnectionBuilder() {
        return this.connectionBuilder;
    }

    public int getOriginatorAddress() {
        return originatorAddress;
    }

    public void setOriginatorAddress(int originatorAddress) {
        this.originatorAddress = originatorAddress;
    }
}
