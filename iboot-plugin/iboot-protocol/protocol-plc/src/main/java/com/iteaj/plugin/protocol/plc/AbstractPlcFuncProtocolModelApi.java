package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.*;

import java.util.LinkedHashMap;
import java.util.Map;
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

    }
}
