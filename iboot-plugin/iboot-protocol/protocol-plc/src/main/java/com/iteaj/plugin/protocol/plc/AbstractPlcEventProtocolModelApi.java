package com.iteaj.plugin.protocol.plc;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.ApiEventType;
import com.iteaj.framework.spi.iot.protocol.*;

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

    }
}
