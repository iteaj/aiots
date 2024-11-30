package com.iteaj.iboot.plugin.protocol.opcua;

import com.iteaj.iboot.plugin.protocol.opcua.driver.OpcuaProtocolSupplier;
import org.springframework.context.annotation.Bean;

public class OpcuaProtocolSupplierAutoConfiguration {

    @Bean
    public OpcuaProtocolSupplier opcuaProtocolSupplier() {
        return new OpcuaProtocolSupplier();
    }
}
