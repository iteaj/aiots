package com.iteaj.iboot.plugin.protocol.dlt645.network.dtu;

import com.iteaj.iboot.plugin.protocol.dlt645.Dlt645Util;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645DtuMessageBody;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645MessageHead;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645Protocol;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.server.protocol.ServerInitiativeSyncProtocol;

import java.io.IOException;

public class Dlt645DtuProtocol extends ServerInitiativeSyncProtocol<Dlt645DtuMessage> implements Dlt645Protocol {

    private Object value;
    private final Dlt645ProtocolType protocolType;

    public Dlt645DtuProtocol(Dlt645ProtocolType protocolType, Dlt645DtuMessage dlt645DtuMessage) {
        this.protocolType = protocolType;
        this.setRequestMessage(dlt645DtuMessage);
    }

    @Override
    protected Dlt645DtuMessage doBuildRequestMessage() throws IOException {
        this.requestMessage().writeBuild();
        return this.requestMessage();
    }

    @Override
    protected void doBuildResponseMessage(Dlt645DtuMessage message) {
        Dlt645MessageHead reqHead = (Dlt645MessageHead) requestMessage().getHead();
        if(getExecStatus() == ExecStatus.success) {
            final Dlt645MessageHead responseHead = (Dlt645MessageHead) message.getHead();
            responseHead.setAddress(reqHead.getAddress());
            responseHead.setType(reqHead.getType());
            String reason = Dlt645Util.validate(message.getMessage(), responseHead.getPrefix());
            if(reason == null) {
                int prefix = responseHead.getPrefix();
                Dlt645ProtocolType protocolType = protocolType();
                this.value = Dlt645Util.getData(message.getMessage(), prefix, protocolType);
            } else {
                setExecStatus(ExecStatus.fail);
                setReason(reason);
            }
        } else {
            setReason(getExecStatus().desc);
        }
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void buildDlt645RequestMessage(Dlt645MessageHead dlt645MessageHead, Dlt645DtuMessageBody dlt645DtuMessageBody) {
        this.setRequestMessage(new Dlt645DtuMessage(dlt645MessageHead, dlt645DtuMessageBody));
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Dlt645ProtocolType protocolType() {
        return this.protocolType;
    }
}
