package com.iteaj.iboot.plugin.protocol.dlt645.network.dtu;

import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645DtuMessageBody;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645Message;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645MessageHead;
import com.iteaj.iot.message.DefaultMessageHead;
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
        return getHead().setMessageId(getChannelId());
    }

    @Override
    public void writeBuild() {
        if(getHead() instanceof Dlt645MessageHead) {
            final Dlt645MessageHead head = (Dlt645MessageHead)getHead();
            final Dlt645DtuMessageBody body = (Dlt645DtuMessageBody)getBody();
            this.setMessage(body.build(head.build(), head.getPrefix()));
        } else {
            super.writeBuild();
        }
    }
}
