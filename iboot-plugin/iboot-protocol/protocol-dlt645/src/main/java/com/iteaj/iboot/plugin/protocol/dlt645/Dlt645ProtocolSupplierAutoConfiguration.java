package com.iteaj.iboot.plugin.protocol.dlt645;

import com.iteaj.iboot.plugin.protocol.dlt645.driver.Dlt645DtuProtocolSupplier;
import com.iteaj.iboot.plugin.protocol.dlt645.network.dtu.Dlt645DtuComponent;
import com.iteaj.iot.config.ConnectProperties;
import org.springframework.context.annotation.Bean;

public class Dlt645ProtocolSupplierAutoConfiguration {

    @Bean
    public Dlt645DtuComponent dlt645Component() {
        ConnectProperties properties = new ConnectProperties(9595);
        return new Dlt645DtuComponent(properties);
    }

    @Bean
    public Dlt645DtuProtocolSupplier dlt645DtuProtocolSupplier() {
        return new Dlt645DtuProtocolSupplier();
    }
}