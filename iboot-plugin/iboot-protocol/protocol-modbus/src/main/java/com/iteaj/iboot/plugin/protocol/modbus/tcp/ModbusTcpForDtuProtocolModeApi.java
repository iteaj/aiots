package com.iteaj.iboot.plugin.protocol.modbus.tcp;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.iboot.plugin.protocol.modbus.AbstractModbusProtocolModelApi;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.consts.ModbusCoilStatus;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuCommonProtocol;

import java.util.Map;
import java.util.function.Consumer;

public class ModbusTcpForDtuProtocolModeApi extends AbstractModbusProtocolModelApi {

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
        int num = 0;
        int start = 0;
        int childSn = 0;
        byte[] writeValue = new byte[0];
        switch (modbusCode) {
            case Read01:
                ModbusTcpForDtuCommonProtocol.buildRead01(arg.getDeviceSn(), childSn, start, num);
                break;
            case Read02:
                ModbusTcpForDtuCommonProtocol.buildRead02(arg.getDeviceSn(), childSn, start, num);
                break;
            case Read03:
                ModbusTcpForDtuCommonProtocol.buildRead03(arg.getDeviceSn(), childSn, start, num);
                break;
            case Read04:
                ModbusTcpForDtuCommonProtocol.buildRead04(arg.getDeviceSn(), childSn, start, num);
                break;
            case Write05:
                ModbusTcpForDtuCommonProtocol.buildWrite05(arg.getDeviceSn(), childSn, start, ModbusCoilStatus.OFF);
                break;
            case Write06:
                ModbusTcpForDtuCommonProtocol.buildWrite06(arg.getDeviceSn(), childSn, start, writeValue);
                break;
            case Write0F:
                ModbusTcpForDtuCommonProtocol.buildWrite0F(arg.getDeviceSn(), childSn, start, writeValue);
                break;
            case Write10:
                ModbusTcpForDtuCommonProtocol.buildWrite10(arg.getDeviceSn(), childSn, start, num, writeValue);
                break;
            default:
                throw new ProtocolInvokeException(getProtocolModel().getCode(), getCode(), "不支持的功能代码["+getCode()+"]");
        }
    }
}
