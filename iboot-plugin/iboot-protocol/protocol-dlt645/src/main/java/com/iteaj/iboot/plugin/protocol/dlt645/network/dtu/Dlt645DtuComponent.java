package com.iteaj.iboot.plugin.protocol.dlt645.network.dtu;

import com.iteaj.iot.AbstractProtocol;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.server.dtu.DtuDecoderServerComponent;
import com.iteaj.iot.server.dtu.DtuMessageAware;
import com.iteaj.iot.server.dtu.DtuMessageType;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;

public class Dlt645DtuComponent extends DtuDecoderServerComponent<Dlt645DtuMessage> {

    public Dlt645DtuComponent(ConnectProperties connectProperties) {
        this(connectProperties, DtuMessageType.HEX);
    }

    public Dlt645DtuComponent(ConnectProperties connectProperties, DtuMessageType messageType) {
        super(connectProperties, new Dlt645DtuMessageAware(messageType));
    }

    public Dlt645DtuComponent(ConnectProperties connectProperties, DtuMessageAware<Dlt645DtuMessage> dtuMessageAwareDelegation) {
        super(connectProperties, dtuMessageAwareDelegation);
    }

    @Override
    protected ClientInitiativeProtocol<Dlt645DtuMessage> doGetProtocol(Dlt645DtuMessage message, ProtocolType type) {
        return null;
    }

    @Override
    public String getName() {
        return "DLT645";
    }

    @Override
    public String getDesc() {
        return "基于DTU网关实现的Dlt645电力协议";
    }

    @Override
    public Dlt645DtuMessage createMessage(byte[] message) {
        return new Dlt645DtuMessage(message);
    }

    @Override
    public AbstractProtocol doGetProtocol(Dlt645DtuMessage message) {
        return remove(message.getChannelId());
    }
}
