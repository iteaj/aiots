package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.plc.PlcClientProtocol;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractPlcEventProtocolModelApi extends AbstractEventProtocolModelApi {

    public AbstractPlcEventProtocolModelApi(String code, String name, String remark, TriggerMode mode) {
        super(code, name, remark, mode);
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        PlcClientProtocol plcClientProtocol = (PlcClientProtocol) protocol;
        try {
            Object value;
            String address = arg.getString(PointProtocolConfig.POINT_ADDRESS);
            switch (this.getCode()) {
                case PlcProtocolConsts.PLC_READ_BOOL:
                    value = plcClientProtocol.readBool(address);
                    break;
                case PlcProtocolConsts.PLC_READ_INT16:
                    value = plcClientProtocol.readInt16(address);
                    break;
                case PlcProtocolConsts.PLC_READ_UINT16:
                    value = plcClientProtocol.readUInt16(address);
                    break;
                case PlcProtocolConsts.PLC_READ_INT32:
                    value = plcClientProtocol.readInt32(address);
                    break;
                case PlcProtocolConsts.PLC_READ_UINT32:
                    value = plcClientProtocol.readUInt32(address);
                    break;
                case PlcProtocolConsts.PLC_READ_INT64:
                    value = plcClientProtocol.readInt64(address);
                    break;
                case PlcProtocolConsts.PLC_READ_FLOAT:
                    value = plcClientProtocol.readFloat(address);
                    break;
                case PlcProtocolConsts.PLC_READ_DOUBLE:
                    value = plcClientProtocol.readDouble(address);
                    break;
                case PlcProtocolConsts.PLC_READ_STRING:
                    short length = arg.getShort(PointProtocolConfig.POINT_NUMBER);
                    value = plcClientProtocol.readString(address, length);
                    break;
                default: throw new ServiceException("未知指令["+this.getCode()+"]");
            }

            result.accept(InvokeResult.status(plcClientProtocol.getExecStatus(), value, plcClientProtocol));
        } catch (Exception e) {
            result.accept(InvokeResult.fail(e.getMessage(), protocol));
        }
    }

}
