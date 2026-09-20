package com.iteaj.iboot.plugin.social.autoconfig;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan({"com.iteaj.iboot.plugin.social"})
@MapperScan({"com.iteaj.iboot.plugin.social.mapper"})
public class SocialAutoConfiguration {

}
