package com.iteaj.iboot.plugin.protocol.dtu;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.consts.ModbusCoilStatus;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuCommonProtocol;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuCommonProtocol;
import com.iteaj.iot.utils.ByteUtil;

import java.nio.charset.StandardCharsets;
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
        String value = arg.getString(PointProtocolConfig.POINT_VALUE);
        int start = arg.getInteger(PointProtocolConfig.POINT_ADDRESS);
        int childSn = Integer.valueOf(arg.getDeviceSn());
        byte[] writeValue = new byte[0];
        ModbusCommonProtocol protocol;
        switch (modbusCode) {
            case Write05:
                Boolean aBoolean = arg.getBoolean(PointProtocolConfig.POINT_VALUE);
                ModbusCoilStatus status = Boolean.TRUE.equals(aBoolean) ? ModbusCoilStatus.ON : ModbusCoilStatus.OFF;
                protocol = this.buildWrite05(arg.getParentDeviceSn(), childSn, start, status);
                break;
            case Write06:
                Short aShort = Short.valueOf(value);
                protocol = this.buildWrite06(arg.getParentDeviceSn(), childSn, start, aShort);
                break;
            case Write0F:
                protocol = this.buildWrite0F(arg.getParentDeviceSn(), childSn, start, writeValue);
                break;
            case Write10:
                writeValue = getValue(arg.getString(AbstractDtuForModbusProtocolSupplier.dataType), value);
                int num = writeValue.length % 2 == 0 ? writeValue.length / 2 : writeValue.length / 2 + 1;
                protocol = this.buildWrite10(arg.getParentDeviceSn(), childSn, start, num, writeValue);
                break;
            default:
                throw new ProtocolInvokeException(getProtocolModel().getCode(), getCode(), "不支持的功能代码["+getCode()+"]");
        }

        return protocol;
    }

    protected abstract ModbusCommonProtocol buildWrite05(String equipCode, int device, int start, ModbusCoilStatus status);
    protected abstract ModbusCommonProtocol buildWrite06(String equipCode, int device, int start, short value);
    protected abstract ModbusCommonProtocol buildWrite0F(String equipCode, int device, int start, byte[] write);
    protected abstract ModbusCommonProtocol buildWrite10(String equipCode, int device, int start, int num, byte[] write);

    private byte[] getValue(String dataType, String value) {
        switch (dataType) {
            case "int": return ByteUtil.getBytes(Integer.valueOf(value));
            case "float": return ByteUtil.getBytes(Float.valueOf(value));
            case "double": return ByteUtil.getBytes(Double.valueOf(value));
            case "short": return ByteUtil.getBytesOfReverse(Short.valueOf(value));
            case "long": return ByteUtil.getBytes(Long.valueOf(value));
            case "hex":
                return ByteUtil.hexToByte(value);
            case "string":
                return value.getBytes(StandardCharsets.UTF_8);
            default: throw new IllegalArgumentException("不支持的值类型["+dataType+"]");
        }
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        try {
            if(protocol instanceof ModbusTcpForDtuCommonProtocol) {
                ((ModbusTcpForDtuCommonProtocol)protocol).request();
                result.accept(InvokeResult.status(((ModbusTcpForDtuCommonProtocol) protocol).getExecStatus(), protocol));
            } else {
                ((ModbusRtuForDtuCommonProtocol)protocol).request();
                result.accept(InvokeResult.status(((ModbusRtuForDtuCommonProtocol) protocol).getExecStatus(), protocol));
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
            result.accept(InvokeResult.fail(e.getMessage(), protocol));
        } catch (Exception e) {
            e.printStackTrace();
            result.accept(InvokeResult.fail("未知错误["+e.getMessage()+"]", protocol));
        }
    }
}
