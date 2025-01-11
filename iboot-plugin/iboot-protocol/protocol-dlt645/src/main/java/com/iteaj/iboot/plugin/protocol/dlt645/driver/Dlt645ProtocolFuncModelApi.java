package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;

import java.util.function.Consumer;

public abstract class Dlt645ProtocolFuncModelApi extends AbstractFuncProtocolModelApi {

    public Dlt645ProtocolFuncModelApi(String code, String name) {
        super(code, name);
    }

    public Dlt645ProtocolFuncModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        return null;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {

    }
}
