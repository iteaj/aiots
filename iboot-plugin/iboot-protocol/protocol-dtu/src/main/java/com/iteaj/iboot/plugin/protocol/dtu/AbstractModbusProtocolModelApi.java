package com.iteaj.iboot.plugin.protocol.dtu;

import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;

import java.util.Map;

public abstract class AbstractModbusProtocolModelApi extends AbstractProtocolModelApi {

    public AbstractModbusProtocolModelApi(String code, String name, ProtocolApiType type) {
        super(code, name, type);
    }

    public AbstractModbusProtocolModelApi(String code, String name, String remark, ProtocolApiType type) {
        super(code, name, remark, type);
    }

    public AbstractModbusProtocolModelApi(String code, String name, ProtocolApiType type, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, type, upConfig, downConfig);
    }

    public AbstractModbusProtocolModelApi(String code, String name, String remark, ProtocolApiType type, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, type, upConfig, downConfig);
    }
}
