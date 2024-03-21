package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.plc.PlcClientProtocol;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractPlcFuncProtocolModelApi extends AbstractFuncProtocolModelApi {


    public AbstractPlcFuncProtocolModelApi(String code, String name, FuncType type) {
        super(code, name, type);
    }

    public AbstractPlcFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        super(code, name, type, remark);
    }

    public AbstractPlcFuncProtocolModelApi(String code, String name, FuncType type, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, type, upConfig, downConfig);
    }

    public AbstractPlcFuncProtocolModelApi(String code, String name, FuncType type, String remark, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, type, remark, upConfig, downConfig);
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        PlcClientProtocol clientProtocol = (PlcClientProtocol) buildProtocol(arg);
        try {
            String value = arg.getString(PlcProtocolConsts.PLC_VALUE);
            String address = arg.getString(PointProtocolConfig.POINT_ADDRESS);
            switch (this.getCode()) {
                case PlcProtocolConsts.PLC_WRITE_BOOL:
                    clientProtocol.write(address, Boolean.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_INT16:
                    clientProtocol.write(address, Short.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_INT32:
                    clientProtocol.write(address, Integer.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_INT64:
                    clientProtocol.write(address, Long.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_FLOAT:
                    clientProtocol.write(address, Float.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_DOUBLE:
                    clientProtocol.write(address, Double.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_STRING:
                    clientProtocol.write(address, value);
                    break;
            }

            result.accept(InvokeResult.status(clientProtocol.getExecStatus(), clientProtocol));
        } catch (Exception e) {
            result.accept(InvokeResult.fail(e.getMessage(), clientProtocol));
        }
    }
}
