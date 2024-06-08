package com.iteaj.iboot.plugin.protocol.modbus.tcp;

import com.iteaj.iboot.plugin.protocol.modbus.AbstractModbusFuncProtocolModelApi;
import com.iteaj.iot.format.DataFormat;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.client.rtu.ModbusRtuClientProtocol;
import com.iteaj.iot.modbus.client.tcp.ModbusTcpClientCommonProtocol;
import com.iteaj.iot.modbus.consts.ModbusCoilStatus;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuCommonProtocol;

public class ModbusTcpFuncProtocolModeApi extends AbstractModbusFuncProtocolModelApi {

    public ModbusTcpFuncProtocolModeApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    protected ModbusCommonProtocol buildWrite05(int device, int start, ModbusCoilStatus status) {
        return ModbusTcpClientCommonProtocol.buildWrite05(device, start, status);
    }

    @Override
    protected ModbusCommonProtocol buildWrite06(int device, int start, short value) {
        return ModbusTcpClientCommonProtocol.buildWrite06(device, start, value);
    }

    @Override
    protected ModbusCommonProtocol buildWrite0F(int device, int start, byte[] write) {
        return ModbusTcpClientCommonProtocol.buildWrite0F(device, start, write);
    }

    @Override
    protected ModbusCommonProtocol buildWrite10(int device, int start, DataFormat format, Object value) {
        if(value instanceof byte[]) {
            byte[] message = (byte[]) value;
            int num = message.length % 2 == 0 ? message.length / 2 : message.length / 2 + 1;
            return ModbusTcpClientCommonProtocol.buildWrite10(device, start, num, message);
        }

        return ModbusTcpClientCommonProtocol.buildWrite10(device, start, format, value);
    }
}
