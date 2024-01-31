package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iot.plc.PlcClientProtocol;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractPlcFuncProtocolModelApi extends AbstractFuncProtocolModelApi {

    public AbstractPlcFuncProtocolModelApi(String code, String name) {
        super(code, name);
    }

    public AbstractPlcFuncProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    public AbstractPlcFuncProtocolModelApi(String code, String name, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, upConfig, downConfig);
    }

    public AbstractPlcFuncProtocolModelApi(String code, String name, String remark, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, upConfig, downConfig);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) throws ProtocolInvokeException {
        PlcClientProtocol protocol = createPlcProtocol(arg);
        try {
            String value = arg.getString(PlcProtocolConsts.PLC_VALUE);
            String address = arg.getString(PlcProtocolConsts.PLC_ADDRESS);
            switch (this.getCode()) {
                case PlcProtocolConsts.PLC_WRITE_BOOL:
                    protocol.write(address, Boolean.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_INT16:
                    protocol.write(address, Short.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_INT32:
                    protocol.write(address, Integer.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_INT64:
                    protocol.write(address, Long.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_FLOAT:
                    protocol.write(address, Float.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_DOUBLE:
                    protocol.write(address, Double.valueOf(value));
                    break;
                case PlcProtocolConsts.PLC_WRITE_STRING:
                    protocol.write(address, value);
                    break;
            }

            result.accept(new InvokeResult().setProtocol(protocol));
        } catch (Exception e) {
            result.accept(new InvokeResult(e.getMessage()).setProtocol(protocol));
        }
    }

    protected abstract PlcClientProtocol createPlcProtocol(ProtocolModelApiInvokeParam arg);
}
