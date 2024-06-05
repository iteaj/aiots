package com.iteaj.iboot.plugin.protocol.modbus;

import com.iteaj.iboot.plugin.protocol.modbus.rtu.ModbusRtuProtocolSupplier;
import com.iteaj.iboot.plugin.protocol.modbus.tcp.ModbusTcpProtocolSupplier;
import org.springframework.context.annotation.Bean;

public class ModbusProtocolSupplierAutoConfiguration {

    @Bean
    public ModbusTcpProtocolSupplier modbusTcpProtocolSupplier() {
        return new ModbusTcpProtocolSupplier();
    }

    @Bean
    public ModbusRtuProtocolSupplier modbusRtuProtocolSupplier() {
        return new ModbusRtuProtocolSupplier();
    }
}
