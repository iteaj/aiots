package com.iteaj.iboot.plugin.protocol.opcua.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaException;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaProtocol;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaVariableProtocol;
import com.iteaj.iot.Protocol;

import java.util.function.Consumer;

public class OpcuaProtocolFuncModelApi extends AbstractFuncProtocolModelApi {

    public OpcuaProtocolFuncModelApi(String code, String name) {
        this(code, name, null);
    }

    public OpcuaProtocolFuncModelApi(String code, String name, String remark) {
        super(code, name, FuncType.W, TriggerMode.passive, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        Protocol protocol = null;
        String address = arg.getString("address");
        OpcuaProtocol.OpcuaProtocolType type = OpcuaProtocol.OpcuaProtocolType.valueOf(getCode());
        if(type == OpcuaProtocol.OpcuaProtocolType.WRITE) {
            String value = arg.getString("value");
            protocol = OpcuaVariableProtocol.write(address, value);
        } else if(type == OpcuaProtocol.OpcuaProtocolType.EXEC_METHOD) {
            //
        }

        return protocol;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        if(protocol instanceof OpcuaProtocol) {
            try {
                OpcuaProtocol opcuaProtocol = (OpcuaProtocol) protocol;
                opcuaProtocol.request(arg.getParentDeviceSn());
            } catch (OpcuaException e) {
                result.accept(InvokeResult.fail(e.getMessage(), protocol));
            } catch (Exception e) {
                e.printStackTrace();
                result.accept(InvokeResult.fail("写入失败", protocol));
            }
        }
    }
}
