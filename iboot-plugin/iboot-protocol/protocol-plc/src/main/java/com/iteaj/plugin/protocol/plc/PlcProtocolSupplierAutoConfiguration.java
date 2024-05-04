package com.iteaj.plugin.protocol.plc;

import com.iteaj.plugin.protocol.plc.omron.OmronProtocolSupplier;
import com.iteaj.plugin.protocol.plc.siemens.SiemensProtocolSupplier;
import org.springframework.context.annotation.Bean;

public class PlcProtocolSupplierAutoConfiguration {

    @Bean
    public OmronProtocolSupplier omronProtocolSupplier() {
        return new OmronProtocolSupplier();
    }

    @Bean
    public SiemensProtocolSupplier siemensProtocolSupplier() {
        return new SiemensProtocolSupplier();
    }
}
