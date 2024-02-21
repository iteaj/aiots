package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.EventTriggerMode;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iot.plc.PlcClientProtocol;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractPlcEventProtocolModelApi extends AbstractEventProtocolModelApi {

    public AbstractPlcEventProtocolModelApi(String code, String name, EventTriggerMode eventType) {
        super(code, name, eventType);
    }

    public AbstractPlcEventProtocolModelApi(String code, String name, String remark, EventTriggerMode eventType) {
        super(code, name, remark, eventType);
    }

    public AbstractPlcEventProtocolModelApi(String code, String name, EventTriggerMode eventType, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, eventType, upConfig, downConfig);
    }

    public AbstractPlcEventProtocolModelApi(String code, String name, String remark, EventTriggerMode eventType, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, eventType, upConfig, downConfig);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) throws ProtocolInvokeException {
        PlcClientProtocol protocol = createPlcProtocol(arg);
        try {
            Object value;
            String address = arg.getString(PointProtocolConfig.POINT_ADDRESS);
            switch (this.getCode()) {
                case PlcProtocolConsts.PLC_READ_BOOL:
                    value = protocol.readBool(address);
                    break;
                case PlcProtocolConsts.PLC_READ_INT16:
                    value = protocol.readInt16(address);
                    break;
                case PlcProtocolConsts.PLC_READ_UINT16:
                    value = protocol.readUInt16(address);
                    break;
                case PlcProtocolConsts.PLC_READ_INT32:
                    value = protocol.readInt32(address);
                    break;
                case PlcProtocolConsts.PLC_READ_UINT32:
                    value = protocol.readUInt32(address);
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
                    short length = arg.getShort(PointProtocolConfig.POINT_NUMBER);
                    value = protocol.readString(address, length);
                    break;
                default: throw new ServiceException("未知指令["+this.getCode()+"]");
            }

            result.accept(InvokeResult.status(protocol.getExecStatus(), value, protocol));
        } catch (Exception e) {
            result.accept(InvokeResult.fail(e.getMessage(), protocol));
        }
    }

    protected abstract PlcClientProtocol createPlcProtocol(ProtocolModelApiInvokeParam arg);
}
