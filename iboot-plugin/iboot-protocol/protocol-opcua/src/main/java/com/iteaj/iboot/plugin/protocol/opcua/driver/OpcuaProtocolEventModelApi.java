package com.iteaj.iboot.plugin.protocol.opcua.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaException;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaProtocol;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaVariableProtocol;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;

import java.util.function.Consumer;

public class OpcuaProtocolEventModelApi extends AbstractEventProtocolModelApi {

    public OpcuaProtocolEventModelApi(String code, String name) {
        this(code, name, null);
    }

    public OpcuaProtocolEventModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        Protocol protocol = null;
        String address = arg.getString("address");
        OpcuaProtocol.OpcuaProtocolType type = OpcuaProtocol.OpcuaProtocolType.valueOf(getCode());
        if(type == OpcuaProtocol.OpcuaProtocolType.READ) {
            protocol = OpcuaVariableProtocol.read(address);
        } else if(type == OpcuaProtocol.OpcuaProtocolType.READ_OBJECT) {
//            protocol
        }

        return protocol;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        if(protocol instanceof OpcuaProtocol) {
            try {
                OpcuaProtocol opcuaProtocol = (OpcuaProtocol) protocol;
                opcuaProtocol.request(arg.getParentDeviceSn());
                result.accept(InvokeResult.success(opcuaProtocol.getValue(), protocol));
            } catch (OpcuaException e) {
                result.accept(InvokeResult.fail(e.getMessage(), protocol));
            } catch (Exception e) {
                e.printStackTrace();
                result.accept(InvokeResult.fail("调用协议失败", protocol));
            }
        }
    }
}
