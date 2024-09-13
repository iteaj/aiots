package com.iteaj.iboot.plugin.protocol.dlt645.protocol;

import com.iteaj.iot.server.dtu.message.DtuServerMessageAbstract;

public class Dlt645DtuMessage extends DtuServerMessageAbstract {

    public Dlt645DtuMessage(byte[] message) {
        super(message);
    }

    public Dlt645DtuMessage(Dlt645MessageHead head, Dlt645DtuMessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message, String equipCode) {
        return null;
    }

    @Override
    public Dlt645MessageHead getHead() {
        return (Dlt645MessageHead) super.getHead();
    }

    @Override
    public Dlt645DtuMessageBody getBody() {
        return (Dlt645DtuMessageBody) super.getBody();
    }
}
