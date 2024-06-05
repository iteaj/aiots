package com.iteaj.iboot.plugin.protocol.modbus.rtu;

import com.iteaj.iboot.plugin.protocol.modbus.AbstractModbusFuncProtocolModelApi;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.client.rtu.ModbusRtuClientProtocol;
import com.iteaj.iot.modbus.consts.ModbusCoilStatus;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuCommonProtocol;

public class ModbusRtuFuncProtocolModelApi extends AbstractModbusFuncProtocolModelApi {

    public ModbusRtuFuncProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    protected ModbusCommonProtocol buildWrite05(int device, int start, ModbusCoilStatus status) {
        return ModbusRtuClientProtocol.buildWrite05(device, start, status);
    }

    @Override
    protected ModbusCommonProtocol buildWrite06(int device, int start, short value) {
        return ModbusRtuClientProtocol.buildWrite06(device, start, value);
    }

    @Override
    protected ModbusCommonProtocol buildWrite0F(int device, int start, byte[] write) {
        return ModbusRtuClientProtocol.buildWrite0F(device, start, write);
    }

    @Override
    protected ModbusCommonProtocol buildWrite10(int device, int start, int num, byte[] write) {
        return ModbusRtuClientProtocol.buildWrite10(device, start, num, write);
    }
}
