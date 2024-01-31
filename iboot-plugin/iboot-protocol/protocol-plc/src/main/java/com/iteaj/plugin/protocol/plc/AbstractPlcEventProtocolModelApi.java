package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.ApiEventType;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iot.plc.PlcClientProtocol;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractPlcEventProtocolModelApi extends AbstractEventProtocolModelApi {

    public AbstractPlcEventProtocolModelApi(String code, String name, ApiEventType eventType) {
        super(code, name, eventType);
    }

    public AbstractPlcEventProtocolModelApi(String code, String name, String remark, ApiEventType eventType) {
        super(code, name, remark, eventType);
    }

    public AbstractPlcEventProtocolModelApi(String code, String name, ApiEventType eventType, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, eventType, upConfig, downConfig);
    }

    public AbstractPlcEventProtocolModelApi(String code, String name, String remark, ApiEventType eventType, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, eventType, upConfig, downConfig);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) throws ProtocolInvokeException {
        PlcClientProtocol protocol = createPlcProtocol(arg);
        try {
            Object value = null;
            String address = arg.getString(PlcProtocolConsts.PLC_ADDRESS);
            switch (this.getCode()) {
                case PlcProtocolConsts.PLC_READ_BOOL:
                    value = protocol.readBool(address);
                    break;
                case PlcProtocolConsts.PLC_READ_INT16:
                    value = protocol.readInt16(address);
                    break;
                case PlcProtocolConsts.PLC_READ_INT32:
                    value = protocol.readInt32(address);
                    break;
                case PlcProtocolConsts.PLC_READ_INT64:
                    value = protocol.readInt64(address);
                    break;
                case PlcProtocolConsts.PLC_READ_FLOAT:
                    value = protocol.readFloat(address);
                    break;
                case PlcProtocolConsts.PLC_READ_DOUBLE:
                    value = protocol.readDouble(address);
                    break;
                case PlcProtocolConsts.PLC_READ_STRING:
                    short length = arg.getShort(PlcProtocolConsts.PLC_NUMBER);
                    value = protocol.readString(address, length);
                    break;
            }

            result.accept(new InvokeResult(value).setProtocol(protocol));
        } catch (Exception e) {
            result.accept(new InvokeResult(e.getMessage()).setProtocol(protocol));
        }
    }

    protected abstract PlcClientProtocol createPlcProtocol(ProtocolModelApiInvokeParam arg);
}
