package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.ProtocolType;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;

public class OpcuaVariableProtocol extends OpcuaProtocol{

    protected OpcuaVariableProtocol(String address, OpcuaProtocolType protocolType) {
        super(address, protocolType);
    }

    protected OpcuaVariableProtocol(String address, Object value, ProtocolType protocolType) {
        super(address, value, protocolType);
    }

    public static OpcuaVariableProtocol read(String address) {
        return new OpcuaVariableProtocol(address, OpcuaProtocolType.READ);
    }

    public static OpcuaVariableProtocol write(String address, Object value) {
        return new OpcuaVariableProtocol(address, value, OpcuaProtocolType.WRITE);
    }

    @Override
    protected Object doInvoke(OpcuaClientWrapper client, NodeId nodeId) throws OpcuaException {
        ProtocolType type = getProtocolType();
        if(type == OpcuaProtocolType.READ){
            DataValue read = client.read(nodeId);
            if(read.getStatusCode() == StatusCode.GOOD) {
                return getValue(read);
            }

            throw new OpcuaException("获取数据失败[status="+read.getStatusCode()+"]");
        } else if(type == OpcuaProtocolType.WRITE){
            client.write(nodeId, getValue());
            return this.getValue();
        } else {
            throw new OpcuaException("不支持的协议类型["+getProtocolType()+"]");
        }
    }

    private Object getValue(DataValue read) {
        Object value = read.getValue().getValue();
        if(value instanceof UNumber) {
            if(value instanceof UByte) {
                return ((UByte) value).shortValue();
            } else if(value instanceof UShort) {
                return ((UShort) value).intValue();
            } else if(value instanceof UInteger) {
                return ((UInteger) value).longValue();
            } else if(value instanceof ULong) {
                return ((ULong) value).longValue();
            }
        } else if(value instanceof DateTime) {
            return ((DateTime) value).getJavaDate();
        }

        return value;
    }
}
