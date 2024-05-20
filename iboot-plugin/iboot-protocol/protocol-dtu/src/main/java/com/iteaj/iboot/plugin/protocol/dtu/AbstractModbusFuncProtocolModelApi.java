package com.iteaj.iboot.plugin.protocol.dtu;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.consts.ModbusCoilStatus;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuCommonProtocol;

import java.util.function.Consumer;

public abstract class AbstractModbusFuncProtocolModelApi extends AbstractFuncProtocolModelApi {

    public AbstractModbusFuncProtocolModelApi(String code, String name) {
        super(code, name);
    }

    public AbstractModbusFuncProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        ModbusCode modbusCode = ModbusCode.valueOf(getCode());
        int num = 0;
        int start = 0;
        int childSn = 0;
        byte[] writeValue = new byte[0];
        ModbusTcpForDtuCommonProtocol protocol;
        switch (modbusCode) {
            case Write05:
                protocol = ModbusTcpForDtuCommonProtocol.buildWrite05(arg.getDeviceSn(), childSn, start, ModbusCoilStatus.OFF);
                break;
            case Write06:
                protocol = ModbusTcpForDtuCommonProtocol.buildWrite06(arg.getDeviceSn(), childSn, start, writeValue);
                break;
            case Write0F:
                protocol = ModbusTcpForDtuCommonProtocol.buildWrite0F(arg.getDeviceSn(), childSn, start, writeValue);
                break;
            case Write10:
                protocol = ModbusTcpForDtuCommonProtocol.buildWrite10(arg.getDeviceSn(), childSn, start, num, writeValue);
                break;
            default:
                throw new ProtocolInvokeException(getProtocolModel().getCode(), getCode(), "不支持的功能代码["+getCode()+"]");
        }

        return protocol;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        ((ModbusTcpForDtuCommonProtocol)protocol).request();
        result.accept(null);
    }
}
