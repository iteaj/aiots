package com.iteaj.iboot.plugin.protocol.dlt645.network.dtu;

import com.iteaj.iot.server.dtu.DefaultDtuMessageAware;
import com.iteaj.iot.server.dtu.DtuMessageType;
import io.netty.buffer.ByteBuf;

public class Dlt645DtuMessageAware extends DefaultDtuMessageAware<Dlt645DtuMessage> {

    public Dlt645DtuMessageAware(DtuMessageType messageType) {
        super(messageType);
    }

    @Override
    protected Dlt645DtuMessage customizeType(String equipCode, byte[] message, ByteBuf msg) {
        return super.customizeType(equipCode, message, msg);
    }
}
