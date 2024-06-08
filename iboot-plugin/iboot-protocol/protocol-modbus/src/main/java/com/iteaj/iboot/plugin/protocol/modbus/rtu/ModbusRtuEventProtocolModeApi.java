package com.iteaj.iboot.plugin.protocol.modbus.rtu;

import com.iteaj.iboot.plugin.protocol.modbus.AbstractModbusEventProtocolModelApi;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.client.rtu.ModbusRtuClientProtocol;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuCommonProtocol;

public class ModbusRtuEventProtocolModeApi extends AbstractModbusEventProtocolModelApi {

    public ModbusRtuEventProtocolModeApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    public ModbusCommonProtocol buildRead01(int device, int start, int num) {
        return ModbusRtuClientProtocol.buildRead01(device, start, num);
    }

    @Override
    public ModbusCommonProtocol buildRead02(int device, int start, int num) {
        return ModbusRtuClientProtocol.buildRead02(device, start, num);
    }

    @Override
    public ModbusCommonProtocol buildRead03(int device, int start, int num) {
        return ModbusRtuClientProtocol.buildRead03(device, start, num);
    }

    @Override
    public ModbusCommonProtocol buildRead04(int device, int start, int num) {
        return ModbusRtuClientProtocol.buildRead04(device, start, num);
    }
}
