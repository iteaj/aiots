package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractPlcProtocolModelApi extends AbstractProtocolModelApi {

    public AbstractPlcProtocolModelApi(String code, String name, ProtocolApiType type) {
        super(code, name, type);
    }

    public AbstractPlcProtocolModelApi(String code, String name, String remark, ProtocolApiType type) {
        super(code, name, remark, type);
    }

    public AbstractPlcProtocolModelApi(String code, String name, ProtocolApiType type, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, type, upConfig, downConfig);
    }

    public AbstractPlcProtocolModelApi(String code, String name, String remark, ProtocolApiType type, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, type, upConfig, downConfig);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) throws ProtocolInvokeException {

    }
}
