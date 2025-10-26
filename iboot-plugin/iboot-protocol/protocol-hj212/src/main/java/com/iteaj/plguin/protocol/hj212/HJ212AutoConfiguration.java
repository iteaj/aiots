package com.iteaj.plguin.protocol.hj212;

import com.iteaj.plguin.protocol.hj212.driver.HJ212ProtocolSupplier;
import org.springframework.context.annotation.Bean;

public class HJ212AutoConfiguration {

    @Bean
    public HJ212ProtocolSupplier hj212ProtocolSupplier() {
        return new HJ212ProtocolSupplier();
    }
}
