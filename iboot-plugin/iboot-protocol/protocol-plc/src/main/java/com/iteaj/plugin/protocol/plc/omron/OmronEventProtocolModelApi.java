package com.iteaj.plugin.protocol.plc.omron;

import com.iteaj.framework.spi.iot.consts.ApiEventType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.plugin.protocol.plc.AbstractPlcEventProtocolModelApi;

import java.util.Map;

public class OmronEventProtocolModelApi extends AbstractPlcEventProtocolModelApi {

    public OmronEventProtocolModelApi(String code, String name) {
        this(code, name, null);
    }

    public OmronEventProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark, ApiEventType.PASSIVE);
    }

    public OmronEventProtocolModelApi(String code, String name, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        this(code, name, null, upConfig, downConfig);
    }

    public OmronEventProtocolModelApi(String code, String name, String remark, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, ApiEventType.PASSIVE, upConfig, downConfig);
    }
}
