package com.iteaj.iboot.plugin.protocol.dtu;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuCommonProtocol;

import java.util.function.Consumer;

public abstract class AbstractModbusEventProtocolModelApi extends AbstractEventProtocolModelApi {

    public AbstractModbusEventProtocolModelApi(String code, String name) {
        super(code, name);
    }

    public AbstractModbusEventProtocolModelApi(String code, String name, String remark) {
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
            case Read01:
                protocol = ModbusTcpForDtuCommonProtocol.buildRead01(arg.getDeviceSn(), childSn, start, num);
                break;
            case Read02:
                protocol = ModbusTcpForDtuCommonProtocol.buildRead02(arg.getDeviceSn(), childSn, start, num);
                break;
            case Read03:
                protocol = ModbusTcpForDtuCommonProtocol.buildRead03(arg.getDeviceSn(), childSn, start, num);
                break;
            case Read04:
                protocol = ModbusTcpForDtuCommonProtocol.buildRead04(arg.getDeviceSn(), childSn, start, num);
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
