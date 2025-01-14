package com.iteaj.iboot.plugin.protocol.dlt645.network.dtu;

import com.iteaj.iboot.plugin.protocol.dlt645.Dlt645DataItem;
import com.iteaj.iboot.plugin.protocol.dlt645.Dlt645Util;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645DtuMessageBody;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645MessageHead;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645Protocol;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.server.protocol.ServerInitiativeSyncProtocol;
import com.iteaj.iot.utils.ByteUtil;

import java.io.IOException;
import java.util.Date;

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
        Dlt645MessageHead head = requestMessage().getHead();
        message.getHead().setAddress(head.getAddress());
        message.getHead().setType(head.getType());
        if(getExecStatus() == ExecStatus.success) {
            String reason = Dlt645Util.validate(message.getMessage(), message.getHead().getPrefix());
            if(reason == null) {
                int prefix = message.getHead().getPrefix();
                Dlt645ProtocolType protocolType = protocolType();
                byte[] bytes = Dlt645Util.getData(message.getMessage(), prefix, protocolType);
                if(bytes != null) {
                    this.value = ByteUtil.bytesToHex(bytes);
                    String hexValue = ByteUtil.bytesToHex(bytes);
                    if(this.protocolType == Dlt645ProtocolType.readReq) {
                        int item = message.getMessage()[prefix + 13] - 0x33;
                        int childItem = message.getMessage()[prefix + 12] - 0x33;
                        if(item == 2) {
                            Dlt645DataItem dataItem = Dlt645DataItem.valueOf(childItem);
                            if(dataItem != null) {
                                this.value = Long.valueOf(hexValue) * dataItem.getResolution();
                                return;
                            }
                        }
                    }

                    this.value = hexValue;
                }
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
