package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645Protocol;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iboot.plugin.protocol.dlt645.network.serial.Dlt645SerialProtocol;

public class Dlt645SerialProtocolEventModelApi extends Dlt645ProtocolEventModelApi{

    public Dlt645SerialProtocolEventModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    protected Dlt645Protocol createProtocol(Dlt645ProtocolType type) {
        return new Dlt645SerialProtocol(type, null);
    }
}
