package com.iteaj.iboot.plugin.protocol.opcua.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaProtocol;
import com.iteaj.iot.Protocol;

import java.util.function.Consumer;

public class OpcuaProtocolFuncModelApi extends AbstractFuncProtocolModelApi {

    public OpcuaProtocolFuncModelApi(String code, String name) {
        super(code, name);
    }

    public OpcuaProtocolFuncModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    public OpcuaProtocolFuncModelApi(String code, String name, FuncType type) {
        super(code, name, type);
    }

    public OpcuaProtocolFuncModelApi(String code, String name, FuncType type, TriggerMode mode, String remark) {
        super(code, name, type, mode, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        String ns = arg.getString("ns");
        String address = arg.getString("address");
        return OpcuaProtocol.write(ns, address);
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {

    }
}
