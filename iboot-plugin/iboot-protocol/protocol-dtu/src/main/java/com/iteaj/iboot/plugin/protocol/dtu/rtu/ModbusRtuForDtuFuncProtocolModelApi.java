package com.iteaj.iboot.plugin.protocol.dtu.rtu;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractModbusFuncProtocolModelApi;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.consts.ModbusCoilStatus;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuCommonProtocol;

import java.util.function.Consumer;

public class ModbusRtuForDtuFuncProtocolModelApi extends AbstractModbusFuncProtocolModelApi {

    public ModbusRtuForDtuFuncProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        return null;
    }

    @Override
    protected ModbusCommonProtocol buildWrite05(String equipCode, int device, int start, ModbusCoilStatus status) {
        return ModbusRtuForDtuCommonProtocol.buildWrite05(equipCode, device, start, status);
    }

    @Override
    protected ModbusCommonProtocol buildWrite06(String equipCode, int device, int start, short value) {
        return ModbusRtuForDtuCommonProtocol.buildWrite06(equipCode, device, start, value);
    }

    @Override
    protected ModbusCommonProtocol buildWrite0F(String equipCode, int device, int start, byte[] write) {
        return ModbusRtuForDtuCommonProtocol.buildWrite0F(equipCode, device, start, write);
    }

    @Override
    protected ModbusCommonProtocol buildWrite10(String equipCode, int device, int start, int num, byte[] write) {
        return ModbusRtuForDtuCommonProtocol.buildWrite10(equipCode, device, start, num, write);
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {

    }
}
