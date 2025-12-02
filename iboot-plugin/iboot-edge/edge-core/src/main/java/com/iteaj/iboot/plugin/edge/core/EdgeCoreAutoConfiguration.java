package com.iteaj.iboot.plugin.edge.core;

import com.iteaj.framework.ProfilesInclude;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ProfilesInclude("edge")
@EnableConfigurationProperties({EdgeConfigProperties.class})
@ComponentScan(basePackages = {"com.iteaj.iboot.plugin.edge.server", "com.iteaj.iboot.plugin.edge.client"})
public class EdgeCoreAutoConfiguration {

}
