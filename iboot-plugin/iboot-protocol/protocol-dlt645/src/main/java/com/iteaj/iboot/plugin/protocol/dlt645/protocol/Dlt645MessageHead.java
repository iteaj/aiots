package com.iteaj.iboot.plugin.protocol.dlt645.protocol;

import com.iteaj.iot.message.DefaultMessageHead;

public class Dlt645MessageHead extends DefaultMessageHead {


    public Dlt645MessageHead(String equipCode, Dlt645ProtocolType type, byte[] message) {
        super(equipCode, type, message);
    }

    @Override
    public Dlt645ProtocolType getType() {
        return (Dlt645ProtocolType) super.getType();
    }
}
