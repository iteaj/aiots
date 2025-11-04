package com.iteaj.plguin.protocol.hj212.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;

import java.util.function.Consumer;

public class HJ212FuncModelApi extends AbstractFuncProtocolModelApi {

    public HJ212FuncModelApi(String code, String name) {
        super(code, name);
    }

    public HJ212FuncModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    public HJ212FuncModelApi(String code, String name, FuncType type) {
        super(code, name, type);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        return null;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {

    }
}
