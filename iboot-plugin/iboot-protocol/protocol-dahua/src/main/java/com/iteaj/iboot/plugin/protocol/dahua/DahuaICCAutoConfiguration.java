package com.iteaj.iboot.plugin.protocol.dahua;

import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.DefaultClient;
import com.dahuatech.icc.oauth.http.IClient;
import com.iteaj.iboot.plugin.protocol.dahua.access.DahuaICCProtocolSupplier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(DahuaProperties.class)
public class DahuaICCAutoConfiguration {

    public static IClient iClient;
    private final DahuaProperties properties;

    public DahuaICCAutoConfiguration(DahuaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public IClient defaultClient() throws ClientException {
        return new DefaultClient(properties.getHost(), properties.getUsername()
                , properties.getPassword(),properties.getPwdClientId(), properties.getPwdClientSecret());
    }

    @Bean
    public DahuaICCProtocolSupplier dahuaICCAccessProtocolSupplier() {
        return new DahuaICCProtocolSupplier();
    }

}
