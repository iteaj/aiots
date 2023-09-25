package com.iteaj.iboot.plugin.protocol.modbus.tcp;

import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuCommonProtocol;

import java.util.Map;
import java.util.function.Consumer;

public class ModbusTcpForDtuProtocolModeApi extends AbstractProtocolModelApi {

    public ModbusTcpForDtuProtocolModeApi(String code, String name, ProtocolApiType type, String remark) {
        super(code, name, type);
        this.setRemark(remark);
    }

    public ModbusTcpForDtuProtocolModeApi(String code, String name, ProtocolApiType type
            , Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, type, upConfig, downConfig);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<Object> result) {
        ModbusCode modbusCode = ModbusCode.valueOf(getCode());
        switch (modbusCode) {
            case Read01:
//                ModbusTcpForDtuCommonProtocol.buildRead01(arg.getDeviceSn(), 0, 0, 0);
                break;
        }
//        new ModbusTcpForDtuCommonProtocol();
    }
}
