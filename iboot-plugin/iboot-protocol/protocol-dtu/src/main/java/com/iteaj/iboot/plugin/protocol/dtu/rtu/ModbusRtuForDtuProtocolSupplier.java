package com.iteaj.iboot.plugin.protocol.dtu.rtu;

import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractDtuForModbusProtocolSupplier;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractModbusEventProtocolModelApi;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractModbusFuncProtocolModelApi;
import com.iteaj.iboot.plugin.protocol.dtu.ModbusProtocolConsts;
import com.iteaj.iboot.plugin.protocol.dtu.tcp.ModbusTcpForDtuFuncProtocolModeApi;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuServerComponent;

public class ModbusRtuForDtuProtocolSupplier extends AbstractDtuForModbusProtocolSupplier<ModbusRtuForDtuServerComponent> {

    public ModbusRtuForDtuProtocolSupplier() {
        super("DTU:MODBUS_RTU");
    }

    @Override
    public String getDesc() {
        return "基于DTU网关的Modbus Rtu协议提供";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        return null;
    }

    @Override
    public ModbusRtuForDtuServerComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        ConnectProperties connectProperties = new ConnectProperties(config.getHost(), config.getPort());
        if(config.getAllIdle() != null) {
            connectProperties.setAllIdleTime(config.getAllIdle());
        } else if(config.getReadIdle() != null) {
            connectProperties.setReaderIdleTime(config.getReadIdle());
        } else if(config.getWriteIdle() != null) {
            connectProperties.setWriterIdleTime(config.getWriteIdle());
        }

        return new ModbusRtuForDtuServerComponent(connectProperties);
    }

    @Override
    protected AbstractModbusFuncProtocolModelApi createFuncApi(String code, String name, String remark) {
        return new ModbusTcpForDtuFuncProtocolModeApi(code, name, remark);
    }

    @Override
    protected AbstractModbusEventProtocolModelApi createEventApi(String code, String name, String remark) {
        return new ModbusRtuForDtuEventProtocolModeApi(code, name, remark);
    }
}
