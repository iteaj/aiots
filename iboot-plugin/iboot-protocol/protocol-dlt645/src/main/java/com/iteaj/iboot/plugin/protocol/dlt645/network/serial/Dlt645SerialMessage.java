package com.iteaj.iboot.plugin.protocol.dlt645.network.serial;

import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645DtuMessageBody;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645MessageHead;
import com.iteaj.iot.client.ClientMessage;

public class Dlt645SerialMessage extends ClientMessage {

    public Dlt645SerialMessage(byte[] message) {
        super(message);
    }

    public Dlt645SerialMessage(Dlt645MessageHead head, Dlt645DtuMessageBody body) {
        super(head, body);
    }

    @Override
    protected MessageHead doBuild(byte[] message) {

        return null;
    }

    @Override
    public void writeBuild() {
        this.setMessage(getBody().build(getHead().build(), getHead().getPrefix()));
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
