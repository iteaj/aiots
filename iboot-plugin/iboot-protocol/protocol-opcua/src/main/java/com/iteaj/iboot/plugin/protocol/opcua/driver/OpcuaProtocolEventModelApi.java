package com.iteaj.iboot.plugin.protocol.opcua.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;

import java.util.function.Consumer;

public class OpcuaProtocolEventModelApi extends AbstractEventProtocolModelApi {
    public OpcuaProtocolEventModelApi(String code, String name) {
        super(code, name);
    }

    public OpcuaProtocolEventModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    public OpcuaProtocolEventModelApi(String code, String name, String remark, TriggerMode triggerMode) {
        super(code, name, remark, triggerMode);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        return null;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {

    }
}
