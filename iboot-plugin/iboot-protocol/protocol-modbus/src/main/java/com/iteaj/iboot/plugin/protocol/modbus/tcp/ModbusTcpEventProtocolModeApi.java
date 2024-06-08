package com.iteaj.iboot.plugin.protocol.modbus.tcp;

import com.iteaj.iboot.plugin.protocol.modbus.AbstractModbusEventProtocolModelApi;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.client.tcp.ModbusTcpClientCommonProtocol;

public class ModbusTcpEventProtocolModeApi extends AbstractModbusEventProtocolModelApi {

    public ModbusTcpEventProtocolModeApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    public ModbusCommonProtocol buildRead01(int device, int start, int num) {
        return ModbusTcpClientCommonProtocol.buildRead01(device, start, num);
    }

    @Override
    public ModbusCommonProtocol buildRead02(int device, int start, int num) {
        return ModbusTcpClientCommonProtocol.buildRead02(device, start, num);
    }

    @Override
    public ModbusCommonProtocol buildRead03(int device, int start, int num) {
        return ModbusTcpClientCommonProtocol.buildRead03(device, start, num);
    }

    @Override
    public ModbusCommonProtocol buildRead04(int device, int start, int num) {
        return ModbusTcpClientCommonProtocol.buildRead04(device, start, num);
    }
}
