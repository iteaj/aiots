package com.iteaj.iboot.plugin.protocol.opcua.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaProtocol;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaVariableProtocol;
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
        Protocol protocol = null;
        String address = arg.getString("address");
        OpcuaProtocol.OpcuaProtocolType type = OpcuaProtocol.OpcuaProtocolType.valueOf(getCode());
        switch (type) {
            case READ:
                protocol = OpcuaVariableProtocol.read(address);
                break;
                case WRITE:
                    String value = arg.getString("value");
                    protocol = OpcuaVariableProtocol.write(address, value);
                    break;
            case EXEC_METHOD:
                break;
            case READ_OBJECT:
                break;
        }

        return protocol;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        if(protocol instanceof OpcuaProtocol) {
            ((OpcuaProtocol) protocol).request(arg.getParentDeviceSn());
        }
    }
}
