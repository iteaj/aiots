package com.iteaj.iboot.plugin.protocol.modbus;

import com.iteaj.iboot.plugin.protocol.modbus.rtu.ModbusRtuForDtuProtocolSupplier;
import com.iteaj.iboot.plugin.protocol.modbus.tcp.ModbusTcpForDtuProtocolSupplier;
import org.springframework.context.annotation.Bean;

public class ModbusProtocolSupplierAutoConfiguration {

    @Bean
    public ModbusTcpForDtuProtocolSupplier modbusTcpForDtuProtocolSupplier() {
        return new ModbusTcpForDtuProtocolSupplier();
    }

    @Bean
    public ModbusRtuForDtuProtocolSupplier modbusRtuForDtuProtocolSupplier() {
        return new ModbusRtuForDtuProtocolSupplier();
    }
}
