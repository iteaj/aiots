package com.iteaj.iboot.plugin.protocol.dtu;

import com.iteaj.framework.spi.iot.data.IModelAttrRealtimeDataService;
import com.iteaj.iboot.plugin.protocol.dtu.mqtt.ModbusOverMqttForDtuProtocolSupplier;
import com.iteaj.iboot.plugin.protocol.dtu.rtu.ModbusRtuForDtuProtocolSupplier;
import com.iteaj.iboot.plugin.protocol.dtu.tcp.ModbusTcpForDtuProtocolSupplier;
import org.springframework.context.annotation.Bean;

public class ModbusProtocolSupplierAutoConfiguration {

    public static IModelAttrRealtimeDataService realtimeDataService;

    public ModbusProtocolSupplierAutoConfiguration(IModelAttrRealtimeDataService realtimeDataService) {
        ModbusProtocolSupplierAutoConfiguration.realtimeDataService = realtimeDataService;
    }

    @Bean
    public ModbusTcpForDtuProtocolSupplier modbusTcpForDtuProtocolSupplier() {
        return new ModbusTcpForDtuProtocolSupplier();
    }

    @Bean
    public ModbusRtuForDtuProtocolSupplier modbusRtuForDtuProtocolSupplier() {
        return new ModbusRtuForDtuProtocolSupplier();
    }

    @Bean
    public ModbusOverMqttForDtuProtocolSupplier modbusOverMqttForDtuProtocolSupplier() {
        return new ModbusOverMqttForDtuProtocolSupplier();
    }

}
