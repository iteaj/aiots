package com.iteaj.plugin.protocol.plc;

import com.iteaj.plugin.protocol.plc.omron.OmronProtocolSupplier;
import org.springframework.context.annotation.Bean;

public class PlcProtocolSupplierAutoConfiguration {

    @Bean
    public OmronProtocolSupplier omronProtocolSupplier() {
        return new OmronProtocolSupplier();
    }

}
