package com.iteaj.plugin.protocol.plc.omron;

import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.plugin.protocol.plc.AbstractPlcFuncProtocolModelApi;

import java.util.Map;

public class OmronFuncProtocolModelApi extends AbstractPlcFuncProtocolModelApi {

    public OmronFuncProtocolModelApi(String code, String name) {
        super(code, name);
    }

    public OmronFuncProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    public OmronFuncProtocolModelApi(String code, String name, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, upConfig, downConfig);
    }

    public OmronFuncProtocolModelApi(String code, String name, String remark, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, upConfig, downConfig);
    }
}
