package com.iteaj.iboot.msn.iot;

import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuServerComponent;
import com.iteaj.iot.modbus.server.dtu.ModbusTcpForDtuServerComponent;
import com.iteaj.iot.plc.omron.OmronComponent;
import com.iteaj.iot.plc.siemens.SiemensS7Component;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@Profile("iot")
@ComponentScan({"com.iteaj.iboot.msn.iot"})
@MapperScan({"com.iteaj.iboot.msn.iot.mapper"})
public class IotAutoConfiguration {

    /**
     * 启用modbus rtu 组件
     * @return
     */
    @Bean
    public ModbusRtuForDtuServerComponent modbusRtuForDtuServerComponent() {
        return new ModbusRtuForDtuServerComponent(new ConnectProperties(7058));
    }

    /**
     * 启用modbus tcp 组件
     * @return
     */
    @Bean
    public ModbusTcpForDtuServerComponent modbusTcpForDtuServerComponent() {
        return new ModbusTcpForDtuServerComponent(new ConnectProperties(7068));
    }

    /**
     * 西门子S7 PLC组件
     * @return
     */
    @Bean
    public SiemensS7Component siemensS7Component() {
        return new SiemensS7Component();
    }

    /**
     * 欧姆龙PLC组件
     * @return
     */
    @Bean
    public OmronComponent omronComponent() {
        return new OmronComponent();
    }
}
