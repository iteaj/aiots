package com.iteaj.plugin.protocol.plc.omron;

import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.plugin.protocol.plc.AbstractPlcProtocolModelApi;

import java.util.Map;

public class OmronProtocolModelApi extends AbstractPlcProtocolModelApi {



    public OmronProtocolModelApi(String code, String name, ProtocolApiType type) {
        super(code, name, type);
    }

    public OmronProtocolModelApi(String code, String name, String remark, ProtocolApiType type) {
        super(code, name, remark, type);
    }

    public OmronProtocolModelApi(String code, String name, ProtocolApiType type, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, type, upConfig, downConfig);
    }

    public OmronProtocolModelApi(String code, String name, String remark, ProtocolApiType type, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, type, upConfig, downConfig);
    }
}
