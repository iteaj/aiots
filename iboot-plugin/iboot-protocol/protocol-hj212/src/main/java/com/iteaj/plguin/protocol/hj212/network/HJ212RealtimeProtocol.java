package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;

import java.util.Map;
import java.util.Set;

/**
 * HJ212数据推送协议
 */
public class HJ212RealtimeProtocol extends ClientInitiativeProtocol<HJ212Message> {

    public HJ212RealtimeProtocol(HJ212Message requestMessage) {
        super(requestMessage);
    }

    @Override
    protected HJ212Message doBuildResponseMessage() {
        return null;
    }

    @Override
    protected void doBuildRequestMessage(HJ212Message requestMessage) {

    }

    public String getItem(String key) {
        return requestMessage().getBody().getItem(key);
    }

    public Set<Map.Entry<String, String>> getItems() {
        return requestMessage().getBody().getData().entrySet();
    }

    public Map<String, String> getData() {
        return requestMessage().getBody().getData();
    }

    @Override
    public ProtocolType protocolType() {
        return requestMessage().getHead().getType();
    }
}
