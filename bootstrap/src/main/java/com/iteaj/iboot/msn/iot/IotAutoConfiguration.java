package com.iteaj.iboot.msn.iot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@Profile("iot")
@ComponentScan({"com.iteaj.iboot.msn.iot"})
@MapperScan({"com.iteaj.iboot.msn.iot.mapper"})
public class IotAutoConfiguration {

    
}
