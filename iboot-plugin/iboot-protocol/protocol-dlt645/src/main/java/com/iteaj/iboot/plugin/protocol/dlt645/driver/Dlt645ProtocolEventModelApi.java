package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;

import java.util.function.Consumer;

public class Dlt645ProtocolEventModelApi extends AbstractEventProtocolModelApi {

    public Dlt645ProtocolEventModelApi(String code, String name) {
        this(code, name, null);
    }

    public Dlt645ProtocolEventModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        return null;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {

    }
}
