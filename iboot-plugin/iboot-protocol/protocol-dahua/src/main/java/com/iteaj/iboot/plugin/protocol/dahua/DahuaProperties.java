package com.iteaj.iboot.plugin.protocol.dahua;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "icc.sdk")
public class DahuaProperties {

    private String host;
    private String username;
    private String password;
    private String grantType;
    private String pwdClientId;
    private String pwdClientSecret;
}
