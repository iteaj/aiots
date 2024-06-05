package com.iteaj.iboot.plugin.protocol.modbus.rtu;

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.iboot.plugin.protocol.modbus.AbstractModbusEventProtocolModelApi;
import com.iteaj.iboot.plugin.protocol.modbus.AbstractModbusFuncProtocolModelApi;
import com.iteaj.iboot.plugin.protocol.modbus.AbstractModbusProtocolSupplier;
import com.iteaj.iot.modbus.client.rtu.ModbusRtuClientComponent;
import com.iteaj.iot.serial.SerialClient;
import com.iteaj.iot.serial.SerialConnectProperties;

public class ModbusRtuProtocolSupplier extends AbstractModbusProtocolSupplier<NetworkConfigImpl.SerialConfig, ModbusRtuClientComponent, SerialClient> {

    private ParamMeta[] GatewayConfig = new ParamMeta[]{
            ParamMeta.buildRequired("com", "串口", "", "").setPlaceholder("请输入串口地址"),
            ParamMeta.buildRequiredNumber("readIdle", "读空闲时长(秒)", 5, "读超时时间"),
    };

    public ModbusRtuProtocolSupplier() {
        super("MODBUS_RTU", TransportProtocol.SERIAL, "用于提供边缘网关Modbus Rtu协议的支持");
    }

    @Override
    public String getDesc() {
        return "Modbus Rtu协议提供";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ModbusRtuClientComponent doCreateComponent(NetworkConfigImpl.SerialConfig config) {
        return new ModbusRtuClientComponent();
    }

    @Override
    public ParamMeta[] getGatewayConfig() {
        return GatewayConfig;
    }

    @Override
    protected AbstractModbusFuncProtocolModelApi createFuncApi(String code, String name, String remark) {
        return new ModbusRtuFuncProtocolModelApi(code, name, remark);
    }

    @Override
    protected AbstractModbusEventProtocolModelApi createEventApi(String code, String name, String remark) {
        return new ModbusRtuEventProtocolModeApi(code, name, remark);
    }

    @Override
    protected SerialClient doCreateClient(NetworkConfigImpl.SerialConfig config, ModbusRtuClientComponent component) {
        SerialConnectProperties connectProperties = new SerialConnectProperties(config.getCom());
        return component.createNewClient(connectProperties);
    }
}
