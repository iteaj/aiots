package com.iteaj.iboot.plugin.protocol.modbus.rtu;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;

import java.util.Map;
import java.util.function.Consumer;

public class ModbusRtuForDtuProtocolModelApi extends AbstractProtocolModelApi {

    public ModbusRtuForDtuProtocolModelApi(String code, String name, ProtocolApiType type, String remark) {
        super(code, name, type);
        this.setRemark(remark);
    }

    public ModbusRtuForDtuProtocolModelApi(String code, String name, ProtocolApiType type, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, type, upConfig, downConfig);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<Object> result) {

    }
}
