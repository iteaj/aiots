package com.iteaj.iboot.plugin.protocol.dahua;

import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.http.DefaultClient;
import com.dahuatech.icc.oauth.http.IClient;
import com.iteaj.iboot.plugin.protocol.dahua.access.DahuaICCAccessProtocolSupplier;
import com.iteaj.iboot.plugin.protocol.dahua.video.DahuaICCVideoProtocolSupplier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@PropertySource({"classpath:application-dahua.yml"})
@EnableConfigurationProperties(DahuaProperties.class)
public class DahuaICCAutoConfiguration {

    public static IClient iClient;
    private final DahuaProperties properties;

    public DahuaICCAutoConfiguration(DahuaProperties properties) {
        this.properties = properties;
    }

    @Bean
    public IClient defaultClient() throws ClientException {
        return iClient = new DefaultClient(properties.getHost(), properties.getUsername()
                , properties.getPassword(),properties.getPwdClientId(), properties.getPwdClientSecret());
    }

    @Bean
    public DahuaICCAccessProtocolSupplier dahuaICCAccessProtocolSupplier() {
        return new DahuaICCAccessProtocolSupplier();
    }

    @Bean
    public DahuaICCVideoProtocolSupplier dahuaICCVideoProtocolSupplier() {
        return new DahuaICCVideoProtocolSupplier();
    }
}
