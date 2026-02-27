package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.ProtocolType;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;

import java.util.List;

public class OpcuaVariableProtocol extends OpcuaProtocol{

    protected OpcuaVariableProtocol(String address, OpcuaProtocolType protocolType) {
        super(address, protocolType);
    }

    public OpcuaVariableProtocol(List<String> addresses, ProtocolType protocolType) {
        super(addresses, protocolType);
    }

    protected OpcuaVariableProtocol(String address, Object value, ProtocolType protocolType) {
        super(address, value, protocolType);
    }

    public static OpcuaVariableProtocol read(String address) {
        return new OpcuaVariableProtocol(address, OpcuaProtocolType.READ);
    }

    public static OpcuaVariableProtocol read(List<String> addresses) {
        return new OpcuaVariableProtocol(addresses, OpcuaProtocolType.READ);
    }

    public static OpcuaVariableProtocol write(String address, Object value) {
        return new OpcuaVariableProtocol(address, value, OpcuaProtocolType.WRITE);
    }

    @Override
    protected Object doInvoke(OpcuaClientWrapper client, NodeId nodeId) throws OpcuaException {
        ProtocolType type = getProtocolType();
        if(type == OpcuaProtocolType.READ){
            return client.read(nodeId);
        } else if(type == OpcuaProtocolType.WRITE){
            client.write(nodeId, getValue());
            return this.getValue();
        } else {
            throw new OpcuaException("不支持的协议类型["+getProtocolType()+"]");
        }
    }

    @Override
    protected Object doInvoke(OpcuaClientWrapper client, List<NodeId> nodeIds) throws OpcuaException {
        return client.read(0, nodeIds);
    }
}
