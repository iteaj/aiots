package com.iteaj.msn.iot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.iteaj.msn.iot"})
@MapperScan({"com.iteaj.msn.iot.mapper"})
public class IotAutoConfiguration {

}
