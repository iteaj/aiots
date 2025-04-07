package com.iteaj.iboot.plugin.script;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.iteaj.iboot.plugin.script"})
@MapperScan({"com.iteaj.iboot.plugin.script.mapper"})
public class ScriptAutoConfiguration {

}
